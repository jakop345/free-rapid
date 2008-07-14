package cz.felk.cvut.erm.fileimport.ermver4;

import cz.felk.cvut.erm.conceptual.NotationType;
import cz.felk.cvut.erm.conceptual.beans.*;
import cz.felk.cvut.erm.conceptual.exception.ISAChildCannotHavePrimaryKeyException;
import cz.felk.cvut.erm.conceptual.exception.IsMemberOfPrimaryKeyException;
import cz.felk.cvut.erm.conceptual.exception.RelationCannotHavePrimaryKeyException;
import cz.felk.cvut.erm.datatype.*;
import cz.felk.cvut.erm.ermodeller.*;
import cz.felk.cvut.erm.event.ResizeEvent;
import cz.felk.cvut.erm.event.ResizeRectangle;
import cz.felk.cvut.erm.typeseditor.UserTypeStorage;
import cz.felk.cvut.erm.typeseditor.UserTypeStorageVector;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * @author Ladislav Vitasek
 */
public class XMLBind {
    private List<Atribute> attrs;
    private List<Integer> attrsPos;
    private WorkingDesktop d;
    private int id;
    private int t, l, tt, ll;
    private UserTypeStorageVector typesVector;
    private String prefix;
    private static final String RESOURCES_SCHEMA_XSD = "resources/schema.xsd";

    public BindSchema loadSchema(File f) throws JAXBException, SAXException {
        //System.getProperties().put("javax.xml.bind.JAXBContext", "com.sun.xml.internal.bind.v2.ContextFactory");
        final JAXBContext ctx = getContext();
        final Unmarshaller unmarshaller = ctx.createUnmarshaller();
        //unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        //SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        //unmarshaller.setSchema(sf.newSchema(XMLBind.class.getResource(RESOURCES_SCHEMA_XSD)));
        //final javax.xml.validation.Schema schema = unmarshaller.getSchema();
        init();
        final JAXBElement rootElement = (JAXBElement) unmarshaller.unmarshal(f);
        return (BindSchema) rootElement.getValue();
    }

    private JAXBContext getContext() throws JAXBException {
        return JAXBContext.newInstance(BindSchema.class.getPackage().getName());
    }

    private void init() {
        ll = 0;
        tt = 0;
        prefix = "";
    }

    public void shiftEntities(int r[]) {
        ll = r[0] + 10;
        tt = r[1] + 10;
        if (ll > tt) //TODO proc ll > tt ?
            ll = 0;
        else
            tt = 0;
    }

    public void loadDesktop(WorkingDesktop d, int id, BindSchema erdoc) throws RelationCannotHavePrimaryKeyException, IsMemberOfPrimaryKeyException, ISAChildCannotHavePrimaryKeyException {
        loadDesktop(d, id, erdoc, new int[]{0, 0});
    }

    public void loadDesktop(WorkingDesktop d, int id, BindSchema erdoc, int[] r) throws RelationCannotHavePrimaryKeyException, IsMemberOfPrimaryKeyException, ISAChildCannotHavePrimaryKeyException {
        shiftEntities(r);
        this.d = d;
        this.id = id;
        loadUserTypes(erdoc);

        final Schema schemaM = (Schema) d.getModel();

        attrs = new Vector<Atribute>();
        attrsPos = new Vector<Integer>();
        String schemaName;
        if (erdoc.isSetName())
            schemaName = erdoc.getName();
        else
            schemaName = "composed";//TODO i18n
        this.prefix = schemaName + "_";
        schemaM.setName(schemaName);

        int schemaID = id + erdoc.getId();
        schemaM.setNotationType(NotationType.values()[erdoc.getNotation()]);

        schemaM.setID(schemaID);

        final List<Object> relationOrEntityList = erdoc.getRelationOrAtributeOrEntity();
        for (Object o : relationOrEntityList) { //vzhledem k tomu, ze poradi elementu je v xml souboru nahodne,
            // ale nejaky pred muze odkazovat s id na nasledujici...
            if (o instanceof BindRelation)
                processRelation((BindRelation) o);
            else if (o instanceof BindEntity)
                processEntity((BindEntity) o);
        }
        for (Object o : relationOrEntityList) {
            if (o instanceof BindAttribute)
                processAttribute((BindAttribute) o);
            else if (o instanceof BindCardinality)
                processCardinality((BindCardinality) o);
            else if (o instanceof BindStrong)
                processStrong((BindStrong) o);
        }
        for (Object o : relationOrEntityList) {
            if (o instanceof BindUnique)
                processUnique((BindUnique) o);
        }

        for (int j = 0; j < attrs.size(); j++)
            attrs.get(j).setPosition(attrsPos.get(j));
        for (Object aV : d.getAllEntities())
            ((EntityConstruct) aV).recalculatePositionsOfAtributes();
    }

    public String getPrefix() {
        return prefix;
    }

    private void processStrong(BindStrong bindStrong) {
        t = tt + bindStrong.getTop();
        l = ll + bindStrong.getLeft();
        int i = id + bindStrong.getEnt();
        int j = id + bindStrong.getChild();
        EntityConstruct ent = d.getEntity(i);
        EntityConstruct child = (EntityConstruct) d.getConceptualObject(j);
        StrongAddiction.createStrongAddiction(ent, child, child.getManager(), l, t);
    }

    private void processRelation(BindRelation bindRelation) {
        t = tt + bindRelation.getTop();
        l = ll + bindRelation.getLeft();
        int w = bindRelation.getWidth();
        int h = bindRelation.getHeight();
        final RelationConstruct rel = d.createRelation(l, t, w, h);
        rel.setID(id + bindRelation.getId());
        final Relation model = (Relation) rel.getModel();
        if (bindRelation.isSetName())
            model.setName(bindRelation.getName());
        if (bindRelation.isSetComment())
            model.setComment(bindRelation.getComment());
    }

    private void processUnique(BindUnique bindUnique) {
        t = tt + bindUnique.getTop();
        l = ll + bindUnique.getLeft();
        EntityConstruct ent = d.getEntity(id + bindUnique.getId());
        final UniqueKeyConstruct uni = ent.createUniqueKey(l, t);
        uni.setID(id + bindUnique.getId());
        UniqueKey model = (UniqueKey) uni.getModel();
        if (bindUnique.isSetName())
            model.setName(bindUnique.getName());
        if (bindUnique.isSetComment())
            model.setComment(bindUnique.getComment());
        final List<Integer> atrList = bindUnique.getAtr();
        for (Integer atrId : atrList) {
            uni.addAtribute(d.getAtribute(id + atrId));
        }
        if (bindUnique.isSetPrimary())
            uni.setPrimary();
    }

    private void processEntity(BindEntity bindEntity) {
        t = tt + bindEntity.getTop();
        l = ll + bindEntity.getLeft();
        int w = bindEntity.getWidth();
        int h = bindEntity.getHeight();
        EntityConstruct ent = d.createEntity(l, t, w, h, null);
        ent.setID(id + bindEntity.getId());
        final Entity entityModel = ent.getModel();

        if (bindEntity.isSetName())
            entityModel.setName(bindEntity.getName());
        if (bindEntity.isSetComment())
            entityModel.setComment(bindEntity.getComment());
        if (bindEntity.isSetConstraints())
            entityModel.setConstraints(bindEntity.getConstraints());
        if (bindEntity.isSetParent()) {
            int i = id + bindEntity.getParent();
            d.getEntity(i).addISAChild(ent, new ResizeEvent(0, 0, 0, 0, new ResizeRectangle(0, 0, 0, 0, 0), null));
        }
    }

    private void processCardinality(BindCardinality bindCardinality) {
        t = tt + bindCardinality.getTop();
        l = ll + bindCardinality.getLeft();
        EntityConstruct ent = d.getEntity(id + bindCardinality.getEnt());
        RelationConstruct rel = d.getRelation(id + bindCardinality.getRel());
        final CardinalityConstruct car = rel.createCardinality(ent, d, l, t);
        car.setID(id + bindCardinality.getId());
        final Cardinality model = (Cardinality) car.getModel();
        if (bindCardinality.isSetName())
            model.setName(bindCardinality.getName());
        if (bindCardinality.isSetComment())
            model.setComment(bindCardinality.getComment());
        if (bindCardinality.isSetArbitrary())
            model.setArbitrary(bindCardinality.isArbitrary());
        if (bindCardinality.isSetMulti())
            model.setMultiCardinality(bindCardinality.isMulti());
        if (bindCardinality.isSetGlue())
            model.setGlue(bindCardinality.isGlue());
    }

    private void processAttribute(BindAttribute bindAttribute) throws IsMemberOfPrimaryKeyException, RelationCannotHavePrimaryKeyException, ISAChildCannotHavePrimaryKeyException {
        t = tt + bindAttribute.getTop();
        l = ll + bindAttribute.getLeft();
        final ConceptualConstructItem cc;
        if (bindAttribute.isSetEnt())
            cc = d.getEntity(id + bindAttribute.getEnt());
        else
            cc = d.getRelation(id + bindAttribute.getRel());

        final AttributeConstruct attrConstruct = cc.createAtribute(l, t);
        attrConstruct.setID(id + bindAttribute.getId());
        final Atribute model = attrConstruct.getModel();
        attrs.add(model);
        if (bindAttribute.isSetName())
            model.setName(bindAttribute.getName());
        if (bindAttribute.isSetComment())
            model.setComment(bindAttribute.getComment());

        model.setDataType(extractDataType(bindAttribute.getDataType()));
        model.setArbitrary(bindAttribute.isArbitrary());
        model.setPrimary(bindAttribute.isPrimary());
        model.setUnique(bindAttribute.isUniq());
        attrsPos.add(bindAttribute.getPosition());
    }


    /**
     * loads user data types
     */
    private void loadUserTypes(BindSchema erdoc) {
        typesVector = new UserTypeStorageVector();
        if (!erdoc.isSetUserType())
            return;
        final List<BindUserType> userTypesList = erdoc.getUserType();
        final DataTypeManager typeManager = DataTypeManager.getInstance();
        //  typeManager.getTypeNames().removeAllElements();

        for (BindUserType bindUserType : userTypesList) {
            final BindDatatypeDef datatypeDef = bindUserType.getDatatypeDef();
            final String dataTypeString = datatypeDef.getDataType();

            final DataType dt = extractDataType(dataTypeString);
            if (datatypeDef.isSetItem() && "Object".equals(dataTypeString)) { //TODO consts
                final List<BindItem> bindItemList = datatypeDef.getItem();
                for (BindItem item : bindItemList) {
                    ((ObjectDataType) dt).addItem(new UserTypeStorage(item.getItemName(), extractDataType(item.getDataType()), null));
                }
                ((ObjectDataType) dt).setUserDefinedTypesVector(typesVector);
            }
            typesVector.addType(new UserTypeStorage(bindUserType.getTypeName(), dt, null));
        }
        for (UserTypeStorage typeStorage : typesVector.getUserTypeStorageVector()) {//TODO nesedi tady ukladani, jsou tu jen jmena a ne jejich typy
            typeManager.addToTypeNames(typeStorage.getTypeName());
        }
    }

    public UserTypeStorageVector getTypesVector() {
        return typesVector;
    }

    private DataType extractDataType(String name) { //TODO consts
        DataType dt;
        String s1;

        if (name.startsWith("VarChar2")) {
            name = name.substring(9, name.length() - 1);
            name = name.trim();
            dt = new Varchar2DataType();
            ((LengthDataType) dt).setLength(Integer.parseInt(name));
        } else if (name.startsWith("Char")) {
            name = name.substring(5, name.length() - 1);
            name = name.trim();
            dt = new FixedCharDataType();
            ((LengthDataType) dt).setLength(Integer.parseInt(name));
        } else if (name.startsWith("Number")) {
            dt = new GeneralNumberDataType();
            s1 = name.substring(7, name.indexOf(","));
            ((GeneralNumberDataType) dt).setPrecision(Integer.parseInt(s1));
            name = name.substring(name.indexOf(",") + 1, name.length() - 1);
            name = name.trim();
            ((GeneralNumberDataType) dt).setScale(Integer.parseInt(name));
        } else if (name.startsWith("Table of")) {
            name = name.substring(9, name.length());
            name = name.trim();
            dt = new NestedTableDataType(extractDataType(name));
        } else if (name.startsWith("Varray")) {
            dt = new VarrayDataType();
            s1 = name.substring(8, name.indexOf(")"));
            ((VarrayDataType) dt).setLength(Integer.parseInt(s1));
            name = name.substring(name.indexOf(")") + 4, name.length());
            name = name.trim();
            ((VarrayDataType) dt).setType(extractDataType(name));
        } else if ("Float".equals(name)) {
            dt = new FloatDataType();
        } else if ("Date".equals(name)) {
            dt = new DateDataType();
        } else if ("Integer".equals(name)) {
            dt = new IntegerDataType();
        } else if ("Object".equals(name)) {
            dt = new ObjectDataType();
        } else
            dt = new UserDefinedDataType(name);
        return dt;
    }

}
