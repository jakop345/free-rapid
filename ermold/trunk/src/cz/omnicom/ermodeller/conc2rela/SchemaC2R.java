package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2obj.*;
import cz.omnicom.ermodeller.conc2obj.interfaces.ObjSchemaProducerObj;
import cz.omnicom.ermodeller.conc2rela.exception.*;
import cz.omnicom.ermodeller.conceptual.beans.*;
import cz.omnicom.ermodeller.datatype.NestedTableDataType;
import cz.omnicom.ermodeller.sql.*;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorage;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorageVector;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Relational schema. It makes all the work while transforming from conceptual schema.
 */
public class SchemaC2R extends ObjectC2R implements ObjSchemaProducerObj {
    /**
     * Relation in the schema.
     *
     * @see cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    private Vector<RelationC2R> relationsC2R = new Vector<RelationC2R>();
    /**
     * User types in the schema.
     *
     * @see cz.omnicom.ermodeller.typeseditor.UserTypeStorageVector
     */
    private UserTypeStorageVector typesVector = null;
    /**
     * Conceptual schema
     *
     * @see cz.omnicom.ermodeller.conceptual.beans.Schema
     */
    private Schema schema = null;
    /**
     * Generate drop clauses.
     */
    protected boolean generateDrop = true;
    /**
     * Shorten prefixes.
     */
    protected boolean shortenPrefixes = true;
    protected int rolePrefixLength = -1;
    protected int constructPrefixLength = -1;


    /**
     * Glue from user defined gluings.
     */
    protected boolean userGlue = true;

    /**
     * Used by <code>VecNumberer</code> while creating primary keys -
     * when parallel foreign primary keys, then must create subnumbers to differ
     * foreign atributes.
     *
     * @see #feedUpOnePrimaryKeyC2R
     */
    private class PKNumberer {
        private int number = 0;
        private PrimaryKeyC2R primaryKeyC2R = null;

        public PKNumberer(PrimaryKeyC2R aPrimaryKeyC2R) {
            this.primaryKeyC2R = aPrimaryKeyC2R;
        }

        /**
         * Returns increased number
         */
        public int getNumber() {
            return ++this.number;
        }

        public PrimaryKeyC2R getPrimaryKeyC2R() {
            return this.primaryKeyC2R;
        }
    }

    /**
     * Used while creating primary keys - when parallel foreign primary keys, then must create subnumbers to differ
     * foreign atributes.
     *
     * @see #feedUpOnePrimaryKeyC2R
     */
    private class VecNumberer extends Vector {
        public boolean contains(PrimaryKeyC2R aPrimaryKeyC2R) {
            for (Enumeration elements = this.elements(); elements.hasMoreElements();) {
                PKNumberer numberer = (PKNumberer) elements.nextElement();
                if (numberer.getPrimaryKeyC2R() == aPrimaryKeyC2R)
                    return true;
            }
            return false;
        }

        public void addPK(PrimaryKeyC2R aPrimaryKeyC2R) {
            this.addElement(new PKNumberer(aPrimaryKeyC2R));
        }

        /**
         * Returns increased number for given primary key - solves unicity of names of foreign atributes.
         */
        public int getNumber(PrimaryKeyC2R aPrimaryKeyC2R) {
            for (Enumeration elements = this.elements(); elements.hasMoreElements();) {
                PKNumberer numberer = (PKNumberer) elements.nextElement();
                if (numberer.getPrimaryKeyC2R() == aPrimaryKeyC2R)
                    return numberer.getNumber();
            }
            return 0;
        }
    }

    /**
     * Creates new relational schema from given conceptual schema.
     *
     * @param aConceptualSchema corresponding conceptual schema
     * @param types             corresponding all user defined data types
     * @param aGenerateDrops    whether generate drop clauses in SQL
     * @param aaShortenPrefixes whether shorten prefixes of atributes
     * @param aUserGlue         whether glue default or by user defined gluings
     * @throws cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     * @throws cz.omnicom.ermodeller.conc2rela.exception.WasNotFoundByConceptualExceptionC2R
     *
     */
    public SchemaC2R(Schema aConceptualSchema, UserTypeStorageVector types, boolean aGenerateDrops, boolean aShortenPrefixes, boolean aUserGlue) throws AlreadyContainsExceptionC2R, WasNotFoundByConceptualExceptionC2R {
        super(new NameC2R(aConceptualSchema.getName()), null);
        schema = aConceptualSchema;
        typesVector = types;
        generateDrop = aGenerateDrops;
        shortenPrefixes = aShortenPrefixes;
        userGlue = aUserGlue;
        feedSchemaC2R(aConceptualSchema);
        // can throw AlreadyContains, WasNotFoundByConceptual
    }

    /**
     * returns names of all nested tables
     */
    private String getNestedTableNames() {
        String names = ",";
        for (Enumeration<UserTypeStorage> e = typesVector.elements(); e.hasMoreElements();) {
            UserTypeStorage u = e.nextElement();
            if (u.getDataType() instanceof NestedTableDataType)
                names += u.getTypeName() + ",";
            /*
           if(u.getDataType() instanceof ObjectDataType)
               names += u.getTypeName()+",";
           */
        }
        return names;
    }

    /**
     * Adds relation.
     *
     * @param aRelation cz.omnicom.ermodeller.conc2rela.RelationC2R
     * @throws cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     */
    protected void addRelationC2R(RelationC2R aRelationC2R) throws AlreadyContainsExceptionC2R {
        if (getRelationsC2R().contains(aRelationC2R))
            throw new AlreadyContainsExceptionC2R(this, aRelationC2R, ListExceptionC2R.RELATIONS_LIST);

        getRelationsC2R().addElement(aRelationC2R);
    }

    /**
     * Used while creating primary keys while creating addiction graph. Adds given entity relation to the apropriate level
     * given by <code>aLlevel</code>. When a Primary key is in some level and is request to add to higher level, then removes
     * it from current and then adds it.
     *
     * @param aEntC2R   cz.omnicom.ermodeller.conc2rela.EntC2R
     * @param aSonGraph java.util.Vector
     * @param aLevel    int
     * @see #createLevelMultiTreeOfEntC2R
     * @see #createPrimaryKeys
     * @see #createOnePrimaryKey
     */
    private void addToLevelEntC2R(EntC2R aEntC2R, Vector<Vector<EntC2R>> aSonGraph, int aLevel) {
        if (aEntC2R.alreadyAddedToSonGraph() && aEntC2R.getLevel() >= aLevel) {
            return;
        }
        if (aSonGraph.size() <= aLevel)
            aSonGraph.addElement(new Vector<EntC2R>()); // adds to the end
        if (aEntC2R.alreadyAddedToSonGraph() && aEntC2R.getLevel() < aLevel) {
            removeFromLevelEntC2R(aEntC2R, aSonGraph, aEntC2R.getLevel());
        }
        (aSonGraph.elementAt(aLevel)).addElement(aEntC2R);
        aEntC2R.setLevel(aLevel);

        Entity entity = (Entity) aEntC2R.getConceptualConstruct();
        // all ISA sons
        for (Enumeration<Entity> elements = entity.getISASons().elements(); elements.hasMoreElements();) {
            Entity son = elements.nextElement();
            EntC2R sonC2R = (EntC2R) findRelationC2RByConceptualConstruct(son);
            addToLevelEntC2R(sonC2R, aSonGraph, aLevel + 1);
        }
        // all strong addiction sons
        for (Enumeration<Entity> elements = entity.getStrongAddictionsSons().elements(); elements.hasMoreElements();) {
            Entity son = elements.nextElement();
            EntC2R sonC2R = (EntC2R) findRelationC2RByConceptualConstruct(son);
            addToLevelEntC2R(sonC2R, aSonGraph, aLevel + 1);
        }
    }

    /**
     * For all relational relations creates their relational foreign keys from conceptual cardinalities.
     *
     * @see cz.omnicom.ermodeller.conc2rela.RelC2R
     * @see cz.omnicom.ermodeller.conc2rela.RelForeignKeyC2R
     */
    protected void createForeignKeysC2R() throws WasNotFoundByConceptualExceptionC2R {
        // every "relation" relation
        for (Enumeration<RelationC2R> elements = getRelationsC2R().elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = elements.nextElement();
            if (relationC2R instanceof RelC2R) {
                RelC2R relC2R = (RelC2R) relationC2R;
                Relation conceptualRelationBean = (Relation) relC2R.getConceptualConstruct();
                // every cardinality
                for (Enumeration<Cardinality> cardinalities = conceptualRelationBean.getCardinalities().elements(); cardinalities.hasMoreElements();) {
                    Cardinality conceptualCardinality = cardinalities.nextElement();
                    Entity entity = conceptualCardinality.getEntity();
                    EntC2R entC2R = (EntC2R) findRelationC2RByConceptualConstruct(entity);
                    if (entC2R == null)
                        throw new WasNotFoundByConceptualExceptionC2R(this, entity, ListByConceptualExceptionC2R.RELATIONS_LIST);
                    PrimaryKeyC2R primaryKeyC2R = entC2R.getPrimaryKeyC2R();
                    try {
                        relC2R.addRelForeignKeyC2R(new RelForeignKeyC2R(relC2R.getSchemaC2R(), relC2R, conceptualCardinality, primaryKeyC2R.getUniqueKeyGroupC2R()));
                    }
                    catch (AlreadyContainsExceptionC2R e) {
                        // cannot be thrown
                    }
                }
            }
        }
    }

    /**
     * Creates addiction graph before creating primary keys. This graph is used to generate in tha apropriate order,
     * first in 1st level then second etc, becouse in the first level are not addicted primary keys while in
     * higher levels are addicted to primary keys in lower levels.
     *
     * @return java.util.Vector
     * @see @addToLevelEntC2R
     */
    protected Vector<Vector<EntC2R>> createLevelMultiTreeOfEntC2R() {
        Vector<Vector<EntC2R>> sonGraph = new Vector<Vector<EntC2R>>(); // vector of vectors
        // reset level counters
        for (Enumeration<RelationC2R> elements = getRelationsC2R().elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = elements.nextElement();
            if (relationC2R instanceof EntC2R) {
                ((EntC2R) relationC2R).resetLevel();
            }
        }
        // create multi-tree
        for (Enumeration<RelationC2R> elements = getRelationsC2R().elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = elements.nextElement();
            if (relationC2R instanceof EntC2R) {
                EntC2R entC2R = (EntC2R) relationC2R;
                Entity entity = (Entity) entC2R.getConceptualConstruct();
                if (!entC2R.alreadyAddedToSonGraph() && !entity.isStrongAddicted() && !entity.isISASon()) {
                    addToLevelEntC2R(entC2R, sonGraph, 0);
                }
            }
        }
        return sonGraph;
    }

    /**
     * Creates one primary key for given entity relation. It creates primary key without foreign atributes.
     * To create full primary key, call <code>feedUpOnePrimaryKeyC2R()<code> method.
     *
     * @param aEntC2R cz.omnicom.ermodeller.conc2rela.EntC2R
     * @see #createPrimaryKeysC2R
     * @see #feedUpOnePrimaryKeyC2R
     */
    protected void createOnePrimaryKeyC2R(EntC2R aEntC2R) throws WasNotFoundByConceptualExceptionC2R {
        Entity entity = (Entity) aEntC2R.getConceptualConstruct();
        UniqueKeyC2R uniqueKeyC2R = null;
        if (!entity.isISASon()) {
            Vector<Atribute> primaryKey = entity.getPrimaryKey();
            uniqueKeyC2R = aEntC2R.findUniqueKeyC2RByConceptualUniqueKey(primaryKey);
            if (uniqueKeyC2R == null)
                throw new WasNotFoundByConceptualExceptionC2R(aEntC2R, null, ListByConceptualExceptionC2R.UNIQUEKEYS_LIST);
        } else {
            try {
                uniqueKeyC2R = new UniqueKeyC2R(aEntC2R.getSchemaC2R(), aEntC2R, null, null, false);
            }
            catch (AlreadyContainsExceptionC2R e) {
                // cannot be thrown
            }
            try {
                aEntC2R.addUniqueKeyC2R(uniqueKeyC2R);
            }
            catch (AlreadyContainsExceptionC2R e) {
                // cannot be thrown
            }
        }
        PrimaryKeyC2R primaryKeyC2R = new PrimaryKeyC2R(aEntC2R.getSchemaC2R(), aEntC2R, uniqueKeyC2R);
        if (entity.isISASon()) {
            // adds parent primary keys for ISA addictions
            Entity isaParent = entity.getISAParent();
            EntC2R isaParentC2R = (EntC2R) findRelationC2RByConceptualConstruct(isaParent);
            if (isaParentC2R == null)
                throw new WasNotFoundByConceptualExceptionC2R(this, isaParent, ListByConceptualExceptionC2R.RELATIONS_LIST);

            primaryKeyC2R.addParentPrimaryKeyC2R(isaParentC2R.getPrimaryKeyC2R());
        }
        if (entity.isStrongAddicted()) {
            // adds parent primary keys for strong addictions
            for (Enumeration<Entity> elements = entity.getStrongAddictionsParents().elements(); elements.hasMoreElements();) {
                Entity strongAddictionParent = elements.nextElement();
                EntC2R strongAddictionParentC2R = (EntC2R) findRelationC2RByConceptualConstruct(strongAddictionParent);
                if (strongAddictionParentC2R == null)
                    throw new WasNotFoundByConceptualExceptionC2R(this, strongAddictionParent, ListByConceptualExceptionC2R.RELATIONS_LIST);

                primaryKeyC2R.addParentPrimaryKeyC2R(strongAddictionParentC2R.getPrimaryKeyC2R());
            }
        }
        aEntC2R.setPrimaryKeyC2R(primaryKeyC2R);
    }

    /**
     * Creates primary keys for all entity relation in the schema.
     * Firstly creates addiction graph, then creates skeleton of primary keys and then
     * feeds up primary keys with foreign atributes.
     *
     * @see #createLevelMultiTreeOfEntC2R
     * @see #createOnePrimaryKeyC2R
     * @see #feedUpOnePrimaryKeyC2R
     */
    protected void createPrimaryKeysC2R() throws WasNotFoundByConceptualExceptionC2R {
        // Create multi-tree of addictions
        Vector<Vector<EntC2R>> sonGraph = createLevelMultiTreeOfEntC2R(); // vector of vectors
        // Go through multi-tree and create all primary keys
        for (Enumeration<Vector<EntC2R>> elements = sonGraph.elements(); elements.hasMoreElements();) {
            Vector<EntC2R> level = elements.nextElement();
            for (Enumeration<EntC2R> entsC2R = level.elements(); entsC2R.hasMoreElements();) {
                EntC2R entC2R = entsC2R.nextElement();
                createOnePrimaryKeyC2R(entC2R); // can throw WasNotFoundByConceptual
            }
        }
        // go through multi-tree and add foreign atributes to primary keys, create foreign keys
        for (Enumeration<Vector<EntC2R>> elements = sonGraph.elements(); elements.hasMoreElements();) {
            Vector<EntC2R> level = elements.nextElement();
            for (Enumeration<EntC2R> entsC2R = level.elements(); entsC2R.hasMoreElements();) {
                EntC2R entC2R = entsC2R.nextElement();
                feedUpOnePrimaryKeyC2R(entC2R);
            }
        }
    }

    /**
     * Creates SQL schema.
     * <p/>
     * For each user defined data type generates drop and create commands.
     * For every relation generates drop, create and alter commands.
     * <blockquote>
     * <pre>
     * schemaSQL.addDropCommand(relation.createDropCommandSQL());
     * schemaSQL.addCreateCommand(relation.createCreateCommandSQL());
     * schemaSQL.addAlterAddCommand(relation.createAlterAddCommandSQL());
     * </pre>
     * </blockquote>
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
    public SchemaSQL createSchemaSQL() {
        SchemaSQL schemaSQL = new SchemaSQL(this);

        if (generateDrop)
            for (Enumeration<UserTypeStorage> types = typesVector.elements(); types.hasMoreElements();) {
                UserTypeStorage uts = types.nextElement();
                schemaSQL.addDropType(new DropTypeSQL(uts.getTypeName()));
            }
        for (Enumeration<UserTypeStorage> types = typesVector.elementsForCreating(UserTypeStorageVector.DIRECT); types.hasMoreElements();) {
            UserTypeStorage uts = types.nextElement();
            if (uts.getDataType() instanceof cz.omnicom.ermodeller.datatype.ObjectDataType)
                schemaSQL.addCreateType(new CreateTypeWithRowsSQL((cz.omnicom.ermodeller.datatype.ObjectDataType) uts.getDataType(), uts.getTypeName()));
            else
                schemaSQL.addCreateType(new CreateTypeWithoutRowsSQL(uts.getDataType(), uts.getTypeName()));
        }
        for (Enumeration<UserTypeStorage> types = typesVector.elementsForCreating(UserTypeStorageVector.INDIRECT); types.hasMoreElements();) {
            UserTypeStorage uts = types.nextElement();
            schemaSQL.addCreateIncompleteType(new CreateIncompleteTypeSQL(uts.getTypeName()));
            if (uts.getDataType() instanceof cz.omnicom.ermodeller.datatype.ObjectDataType)
                schemaSQL.addCreateType(new CreateTypeWithRowsSQL((cz.omnicom.ermodeller.datatype.ObjectDataType) uts.getDataType(), uts.getTypeName()));
            else
                schemaSQL.addCreateType(new CreateTypeWithoutRowsSQL(uts.getDataType(), uts.getTypeName()));
        }
        for (Enumeration<RelationC2R> relations = getRelationsC2R().elements(); relations.hasMoreElements();) {
            RelationC2R relation = relations.nextElement();
            if (generateDrop)
                schemaSQL.addDropCommand(relation.createDropCommandSQL());
            schemaSQL.addCreateCommand(relation.createCreateCommandSQL(typesVector));
            schemaSQL.addAlterAddCommand(relation.createAlterAddCommandSQL());
        }
        return schemaSQL;
    }


    /**
     * Creates object-realation script.
     * <p/>
     * For each user defined data type generates drop and create commands.
     * For every relation generates drop, create and alter commands.
     *
     * @return cz.omnicom.ermodeller.conc2rela.SchemaObj
     */
    public SchemaObj createSchemaObj() {
        SchemaObj schemaObj = new SchemaObj(this);

        if (generateDrop)
            for (Enumeration<UserTypeStorage> types = typesVector.elements(); types.hasMoreElements();) {
                UserTypeStorage uts = types.nextElement();
                schemaObj.addDropTypeObj(new DropTypeObj(uts.getTypeName()));
            }
        for (Enumeration<UserTypeStorage> types = typesVector.elementsForCreating(UserTypeStorageVector.DIRECT); types.hasMoreElements();) {
            UserTypeStorage uts = types.nextElement();
            if (uts.getDataType() instanceof cz.omnicom.ermodeller.datatype.ObjectDataType)
                schemaObj.addCreateTypeObj(new CreateTypeWithRowsObj((cz.omnicom.ermodeller.datatype.ObjectDataType) uts.getDataType(), uts.getTypeName()));
            else
                schemaObj.addCreateTypeObj(new CreateTypeWithoutRowsObj(uts.getDataType(), uts.getTypeName()));
        }
        for (Enumeration<UserTypeStorage> types = typesVector.elementsForCreating(UserTypeStorageVector.INDIRECT); types.hasMoreElements();) {
            UserTypeStorage uts = types.nextElement();
            schemaObj.addCreateIncompleteTypeObj(new CreateIncompleteTypeObj(uts.getTypeName()));
            if (uts.getDataType() instanceof cz.omnicom.ermodeller.datatype.ObjectDataType)
                schemaObj.addCreateTypeObj(new CreateTypeWithRowsObj((cz.omnicom.ermodeller.datatype.ObjectDataType) uts.getDataType(), uts.getTypeName()));
            else
                schemaObj.addCreateTypeObj(new CreateTypeWithoutRowsObj(uts.getDataType(), uts.getTypeName()));
        }
        for (Enumeration<RelationC2R> relations = getRelationsC2R().elements(); relations.hasMoreElements();) {

            RelationC2R relation = relations.nextElement();
            if (generateDrop) {
                schemaObj.addDropCommandObj(relation.createDropCommandObj());
                schemaObj.addDropTypeObj(new DropTypeObj(relation.getNameC2R() + "_t"));
            }
            schemaObj.addCommandTypeObj(relation.createCommandTypeObj(typesVector));
            schemaObj.addCreateCommandObj(relation.createCreateCommandObj(typesVector));

            for (Enumeration<AtributeC2R> atributes = relation.getAtributesC2R().elements(); atributes.hasMoreElements();) {
                boolean isForeign = false;
                boolean isRel = false;
                AtributeC2R atribute = atributes.nextElement();
                if (relation instanceof RelRelationC2R) {
                    isRel = !((RelRelationC2R) relation).getGlued();
                    for (Object rel : ((RelRelationC2R) relation).getRelForeignKeysC2R()) {
                        if (((RelForeignKeyC2R) rel).getAtributesC2R().contains(atribute)) {
                            isForeign = true;
                            break;
                        }
                    }
                }
                for (Object o : relation.getEntForeignKeysC2R()) {
                    if (((EntForeignKeyC2R) o).getAtributesC2R().contains((atribute))) {
                        isForeign = true;
                        break;
                    }
                }

                if (isForeign && !isRel) {
                    AlterReferenceType c = new AlterReferenceType(atribute);
                    AlterAddCommandObj a = new AlterAddCommandObj(relation, atribute);
                    schemaObj.addAlterAddCommandObj(a);
                    schemaObj.addReferenceType(c);
                } else if (isForeign) {
                    AlterAddCommandObj a = new AlterAddCommandObj(relation, atribute);
                    schemaObj.addAlterAddCommandObj(a);
                }
            }

        }
        return schemaObj;
    }

    /**
     * Counts minimal length of names of roles, when names of roles differ.
     *
     * @return int
     */
    protected int discoverMinCardPrefix() {
        int result = 0;
        Vector<String> names = new Vector<String>();
        for (Enumeration<Relation> relations = schema.getRelations().elements(); relations.hasMoreElements();) {
            Vector<Cardinality> rCards = (relations.nextElement()).getCardinalities();
            for (Enumeration<Cardinality> cards = rCards.elements(); cards.hasMoreElements();) {
                names.addElement((cards.nextElement()).getName());
            }
        }
        for (int i = 0; i < names.size(); i++) {
            String refName = names.elementAt(i);
            for (int j = i + 1; j < names.size(); j++) {
                String compName = names.elementAt(j);
                int compLen = (refName.length() < compName.length()) ? refName.length() : compName.length();
                int min;
                boolean equal = true;
                for (min = 1; min < compLen && equal; min++) {
                    equal = refName.regionMatches(true, 0, compName, 0, min);
                }
                if (!equal)
                    min--;
                else
                    min++;
                if (result < min)
                    result = min;
            }
        }
        return result;
    }

    /**
     * Counts minimal length of names of roles, when names of roles differ.
     *
     * @return int
     */
    protected int discoverMinConstructPrefix() {
        int result = 0;
        Vector<String> names = new Vector<String>();
        for (Enumeration<Relation> relations = schema.getRelations().elements(); relations.hasMoreElements();) {
            names.addElement((relations.nextElement()).getName());
        }
        for (Enumeration<Entity> entities = schema.getEntities().elements(); entities.hasMoreElements();) {
            names.addElement((entities.nextElement()).getName());
        }
        for (int i = 0; i < names.size(); i++) {
            String refName = names.elementAt(i);
            for (int j = i + 1; j < names.size(); j++) {
                String compName = names.elementAt(j);
                int compLen = (refName.length() < compName.length()) ? refName.length() : compName.length();
                int min;
                boolean equal = true;
                for (min = 1; min < compLen && equal; min++) {
                    equal = refName.regionMatches(true, 0, compName, 0, min);
                }
                if (!equal)
                    min--;
                if (result < min)
                    result = min;
            }
        }
        return result;
    }

    /**
     * Creates relational schema from conceptual schema. Firstly creates skeletons of entity relations,
     * then their primary keys, then relational relations their foreign keys and then optional gluing
     *
     * @see cz.omnicom.ermodeller.conc2rela.EntRelationC2R
     * @see #createPrimaryKeysC2R
     * @see cz.omnicom.ermodeller.conc2rela.RelRelationC2R
     * @see #createForeignKeysC2R
     * @see #glueRelationsC2R
     */
    protected void feedSchemaC2R(Schema aConceptualSchema) throws AlreadyContainsExceptionC2R, WasNotFoundByConceptualExceptionC2R {
        synchronized (aConceptualSchema) {
            // count minimum length of prefixes
            rolePrefixLength = discoverMinCardPrefix();
            constructPrefixLength = discoverMinConstructPrefix();
            // creates skeletons of all "entity" relations
            for (Enumeration<Entity> elements = aConceptualSchema.getEntities().elements(); elements.hasMoreElements();) {
                try {
                    // can throw WasNotFoundByConceptual
                    addRelationC2R(new EntRelationC2R(this, elements.nextElement()));
                }
                catch (AlreadyContainsExceptionC2R e) {
                    // cannot be thrown
                }
            }
            // adds remaining features to "entity" relations (primary keys and foreign keys)
            createPrimaryKeysC2R(); // can throw WasNotFoundByConceptual

            // creates skeletons of all "relation" relations
            for (Enumeration<Relation> elements = aConceptualSchema.getRelations().elements(); elements.hasMoreElements();) {
                try {
                    addRelationC2R(new RelRelationC2R(this, elements.nextElement()));
                }
                catch (AlreadyContainsExceptionC2R e) {
                    // cannot be thrown
                }
            }
            // adds remaining features to "relation" relations (foreign keys)
            createForeignKeysC2R();
            // do gluing
            glueRelationsC2R();
            // set names for unique keys
            nameUniqueKeys();
            // set names for primary keys
            namePrimaryKeys();
            // set names for foreign keys
            nameForeignKeys();
        }
    }

    /**
     * Fillfulls the primary key of given entity. Discovers whether paralell foreign primary keys (created from addictions)
     * exists. If exists, then uses subnumbers to differ new foreign atributes. For each atribute of each
     * foreign primary key creates new corresponding atribute in given entity.
     *
     * @param aEntRelationC2R cz.omnicom.ermodeller.conc2rela.EntRelationC2R
     */
    protected void feedUpOnePrimaryKeyC2R(EntC2R aEntC2R) {
        VecNumberer numberers = new VecNumberer();
        PrimaryKeyC2R primaryKeyC2R = aEntC2R.getPrimaryKeyC2R();
        // discovering parallel strong addictions
        for (Enumeration elements = primaryKeyC2R.getParentsPK().elements(); elements.hasMoreElements();) {
            PrimaryKeyC2R parentPrimaryKeyC2R = (PrimaryKeyC2R) elements.nextElement();
            if (primaryKeyC2R.existParallelParentPrimaryKeyC2R(parentPrimaryKeyC2R)) {
                if (!numberers.contains(parentPrimaryKeyC2R))
                    numberers.addPK(parentPrimaryKeyC2R);
            }
        }
        // creating foreign atributes
        for (Enumeration elements = primaryKeyC2R.getParentsPK().elements(); elements.hasMoreElements();) {
            PrimaryKeyC2R parentPrimaryKeyC2R = (PrimaryKeyC2R) elements.nextElement();
            String prefix;
            int number = -1;
            if (primaryKeyC2R.existParallelParentPrimaryKeyC2R(parentPrimaryKeyC2R)) {
                // prefix must be numbered
                number = numberers.getNumber(parentPrimaryKeyC2R);
            }
            EntForeignKeyC2R entForeignKeyC2R = new EntForeignKeyC2R(aEntC2R.getSchemaC2R(), aEntC2R, parentPrimaryKeyC2R);
            for (Enumeration atributes = parentPrimaryKeyC2R.getAtributesC2R().elements(); atributes.hasMoreElements();) {
                AtributeC2R parentAtributeC2R = (AtributeC2R) atributes.nextElement();
                if (shortenPrefixes) {
                    prefix = parentAtributeC2R.getRelationC2R().getNameC2R().getName();
                    prefix = prefix.substring(0, (prefix.length() < this.constructPrefixLength) ? prefix.length() : this.constructPrefixLength);
                } else
                    prefix = parentAtributeC2R.getRelationC2R().getNameC2R().getName();
                AtributeC2R newAtributeC2R = new AtributeC2R(aEntC2R.getSchemaC2R(), aEntC2R, parentAtributeC2R.getConceptualAtribute(), prefix, parentAtributeC2R);
                if (number > -1)
                    newAtributeC2R.addSubNumberToNameC2R(number);
                try {
                    primaryKeyC2R.getUniqueKeyGroupC2R().addAtributeC2R(newAtributeC2R);
                    entForeignKeyC2R.addForeignAtributeC2R(newAtributeC2R);
                    aEntC2R.addAtributeC2R(newAtributeC2R);
                }
                catch (AlreadyContainsExceptionC2R e) {
                    // cannot be thrown
                }
            }
            try {
                aEntC2R.addEntForeignKeyC2R(entForeignKeyC2R);
            }
            catch (AlreadyContainsExceptionC2R e) {
                // cannot be thrown
            }
        }
    }

    /**
     * Find relation by corresponding conceptual construct.
     *
     * @param aConstruct cz.omnicom.ermodeller.conceptual.ConceptualConstruct
     * @return cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    protected RelationC2R findRelationC2RByConceptualConstruct(ConceptualConstruct aConceptualConstruct) {
        for (Enumeration<RelationC2R> elements = getRelationsC2R().elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = elements.nextElement();
            if (relationC2R.getConceptualConstruct() == aConceptualConstruct)
                return relationC2R;
        }
        return null;
    }

    /**
     * @return java.util.Vector
     */
    public Vector<RelationC2R> getRelationsC2R() {
        if (relationsC2R == null)
            relationsC2R = new Vector<RelationC2R>();
        return relationsC2R;
    }

    /**
     * Glues relations through relational foreign keys, which are marked to be glued.
     * <p/>
     * <blockquote>
     * <pre>
     * For each relational relation relC2R {
     *     Vector gluedRelationsC2R = relC2R.glueC2R(); // can throw AlreadyContains, WasNotFoundByConceptual
     *     recoverC2R(gluedRelationsC2R);
     * }
     * </pre>
     * </blockquote>
     *
     * @see cz.omnicom.ermodeller.conc2rela.RelC2R#glueC2R
     * @see #recoverC2R
     */
    protected void glueRelationsC2R() throws AlreadyContainsExceptionC2R {
        Vector relations = (Vector) getRelationsC2R().clone();
        for (Enumeration elements = relations.elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = (RelationC2R) elements.nextElement();
            if (relationC2R instanceof RelC2R) {
                RelC2R relC2R = (RelC2R) relationC2R;
                // glues all desired relations to this one
                Vector gluedRelationsC2R = relC2R.glueC2R(); // can throw AlreadyContains, WasNotFoundByConceptual
                // removes glued relations
                recoverC2R(gluedRelationsC2R);
            }
        }
    }

    /**
     * Generates names for foreign keys.
     */
    protected void nameForeignKeys() {
        for (Enumeration<RelationC2R> relations = getRelationsC2R().elements(); relations.hasMoreElements();) {
            RelationC2R relation = relations.nextElement();
            for (Enumeration<EntForeignKeyC2R> entFKs = relation.getEntForeignKeysC2R().elements(); entFKs.hasMoreElements();) {
                EntForeignKeyC2R entFK = entFKs.nextElement();
                entFK.setNameC2R(new NameC2R("FK_" + relation.getNameC2R() + "_" + relation.getCountFK()));
            }
            if (relation instanceof RelC2R) {
                RelC2R rel = (RelC2R) relation;
                for (Enumeration relFKs = rel.getRelForeignKeysC2R().elements(); relFKs.hasMoreElements();) {
                    RelForeignKeyC2R relFK = (RelForeignKeyC2R) relFKs.nextElement();
                    relFK.setNameC2R(new NameC2R("FK_" + relation.getNameC2R() + "_" + relation.getCountFK()));
                }
            }
        }
    }

    /**
     * Generates names for primary keys.
     */
    protected void namePrimaryKeys() {
        for (Enumeration<RelationC2R> relations = getRelationsC2R().elements(); relations.hasMoreElements();) {
            RelationC2R relation = relations.nextElement();
            PrimaryKeyC2R pk = relation.getPrimaryKeyC2R();
            if (pk != null && pk.getNameC2R() == null)
                pk.setNameC2R(new NameC2R("PK_" + relation.getNameC2R()));
        }
    }

    /**
     * Generates names for unique keys.
     */
    protected void nameUniqueKeys() {
        for (Enumeration<RelationC2R> relations = getRelationsC2R().elements(); relations.hasMoreElements();) {
            RelationC2R relation = relations.nextElement();
            for (Enumeration<UniqueKeyC2R> relUNQs = relation.getUniqueKeysC2R().elements(); relUNQs.hasMoreElements();) {
                UniqueKeyC2R unq = relUNQs.nextElement();
                if (!unq.isPrimaryKeyC2R() && unq.getNameC2R() == null && unq.getAtributesC2R().size() > 0)
                    unq.setNameC2R(new NameC2R("UNQ_" + relation.getNameC2R() + "_" + relation.getCountUNQ()));

            }
        }
    }

    /**
     * After gluing removes glued relations.
     *
     * @param gluedRelationsC2R java.util.Vector
     */
    protected void recoverC2R(Vector gluedRelationsC2R) {
        for (Enumeration elements = gluedRelationsC2R.elements(); elements.hasMoreElements();) {
            RelationC2R relationC2R = (RelationC2R) elements.nextElement();
            try {
                removeRelationC2R(relationC2R);
            }
            catch (WasNotFoundExceptionC2R e) {
                // never mind
            }
        }
    }

    /**
     * Used while creating primary keys while creating addiction graph. Removes given entity relation from the apropriate level
     * given by <code>aLevel</code>.
     *
     * @param aEntC2R   cz.omnicom.ermodeller.conc2rela.EntC2R
     * @param aSonGraph java.util.Vector
     * @param aLevel    int
     * @see #addToLevelEntC2R
     */
    private void removeFromLevelEntC2R(EntC2R aEntC2R, Vector<Vector<EntC2R>> aSonGraph, int aLevel) {
        (aSonGraph.elementAt(aLevel)).removeElement(aEntC2R);
        aEntC2R.setLevel(aLevel);
    }

    /**
     * Removes relation from the schema.
     *
     * @param relationC2R cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    protected void removeRelationC2R(RelationC2R relationC2R) throws WasNotFoundExceptionC2R {
        if (!getRelationsC2R().removeElement(relationC2R))
            throw new WasNotFoundExceptionC2R(this, relationC2R, ListExceptionC2R.RELATIONS_LIST);
    }
}