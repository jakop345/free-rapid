package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.sql.*;
import cz.omnicom.ermodeller.sql.interfaces.CheckRowProducer;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Records gluing. Collects atributes into groups how they was created.
 */
public class GlueRecord implements CheckRowProducer {
    /**
     * Owner of record
     */
    private RelC2R relation = null;
    /**
     * Relation's atributes of its own.
     */
    private Vector skeletAtributesC2R = new Vector();
    /**
     * Glued relations records.
     */
    private final Vector gluedRelationsC2R = new Vector();
    /**
     * Not glued relations records.
     */
    private final Vector notGluedRelationsC2R = new Vector();

    /**
     * Constructor.
     *
     * @param aRelRelationC2R owner - skelet
     */
    public GlueRecord(RelC2R aRelC2R) {
        this.skeletAtributesC2R = (Vector) aRelC2R.getAtributesC2R().clone();
        this.relation = aRelC2R;
    }

    /**
     * Adds record to glued records.
     *
     * @param aGluedRelationRecord cz.omnicom.ermodeller.conc2rela.GluedRelationRecord
     */
    protected void addGluedRelationC2R(GluedRelationRecord aGluedRelationRecord) {
        getGluedRelationsC2R().addElement(aGluedRelationRecord);
    }

    /**
     * Adds record to not glued records.
     *
     * @param aGluedRelationRecord cz.omnicom.ermodeller.conc2rela.GluedRelationRecord
     */
    protected void addNotGluedRelationC2R(GluedRelationRecord aNotGluedRelationRecord) {
        getNotGluedRelationsC2R().addElement(aNotGluedRelationRecord);
    }

    /**
     * Returns SQL check row.
     *
     * @return CheckRow
     */
    public CheckRow createCheckRow() {
        return new GlueCheckRow(createORCheck());
    }

    /**
     * Creates vector of atributes with property set that these atributes are checked to be NOT NULL.
     *
     * @param aAtributesC2R atributes
     * @return IsNotNullAtributeGroupVector
     */
    public static IsNotNullAtributeGroupVector createNotNullVecAtributes(Vector aAtributesC2R) {
        if (aAtributesC2R.isEmpty())
            return null;
        IsNotNullAtributeGroupVector result = new IsNotNullAtributeGroupVector();
        for (Enumeration elements = aAtributesC2R.elements(); elements.hasMoreElements();) {
            AtributeC2R atributeC2R = (AtributeC2R) elements.nextElement();
            if (atributeC2R.getArbitraryBeforeGluing())
                result.addAtributeC2R(atributeC2R);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    /**
     * Creates vector of atributes with property set that these atributes are checked to be NULL.
     *
     * @param aAtributesC2R atributes
     * @return IsNotNullAtributeGroupVector
     */
    public static IsNullAtributeGroupVector createNullVecAtributes(Vector aAtributesC2R) {
        if (aAtributesC2R.isEmpty())
            return null;
        IsNullAtributeGroupVector result = new IsNullAtributeGroupVector();
        for (Enumeration elements = aAtributesC2R.elements(); elements.hasMoreElements();) {
            AtributeC2R atributeC2R = (AtributeC2R) elements.nextElement();
            result.addAtributeC2R(atributeC2R);
        }
        return result;
    }

    /**
     * Creates ORCheck which enforces conceptual conditions after gluing.
     *
     * @return cz.omnicom.ermodeller.sql.ORCheck
     */
    protected ORCheck createORCheck() {
        ORCheck orCheck = new ORCheck();

        // create check - all atributes (recently not null) are not null
        RelGlueControlPartOfCheck allNotNull = new RelGlueControlPartOfCheck(this.relation);
        IsNotNullAtributeGroupVector vecSkeletNotNull = createNotNullVecAtributes(getSkeletAtributesC2R());
        if (vecSkeletNotNull != null)
            allNotNull.addGroupAtributes(vecSkeletNotNull);
        for (Enumeration elements = getNotGluedRelationsC2R().elements(); elements.hasMoreElements();) {
            IsNotNullAtributeGroupVector vecNotGluedNotNull = createNotNullVecAtributes(((GluedRelationRecord) elements.nextElement()).getGluedAtributesC2R());
            if (vecNotGluedNotNull != null)
                allNotNull.addGroupAtributes(vecNotGluedNotNull);
        }
        for (Enumeration elements = getGluedRelationsC2R().elements(); elements.hasMoreElements();) {
            IsNotNullAtributeGroupVector vecGluedNotNull = createNotNullVecAtributes(((GluedRelationRecord) elements.nextElement()).getGluedAtributesC2R());
            if (vecGluedNotNull != null)
                allNotNull.addGroupAtributes(vecGluedNotNull);
        }
        orCheck.addORPartOfCheck(allNotNull);

        // create checks - for every (0,1) glued relation
        for (Enumeration gluedRelations = getGluedRelationsC2R().elements(); gluedRelations.hasMoreElements();) {
            GluedRelationRecord gluedRelationRecord = (GluedRelationRecord) gluedRelations.nextElement();
            RelationC2R gluedRelationC2R = gluedRelationRecord.getRelationC2R();
            boolean generate = !gluedRelationRecord.getArbitraryCardinality();
            if (gluedRelationC2R instanceof RelC2R)
                generate = generate || ((RelC2R) gluedRelationC2R).getGluedNonArbitrary();
            if (generate) {
                // glued with (0,1)
                EntGlueControlPartOfCheck checkPart = new EntGlueControlPartOfCheck(gluedRelationC2R);
                if (gluedRelationC2R instanceof RelC2R && ((RelC2R) gluedRelationC2R).getGlued()) {
                    // which WAS created by gluing before
                    checkPart.addSubORChecks(((RelC2R) gluedRelationC2R).getGlueRecord().createORCheck());
                } else {
                    // in record glued relation which WAS NOT created by gluing before
                    IsNotNullAtributeGroupVector vecNotNull = createNotNullVecAtributes(gluedRelationRecord.getGluedAtributesC2R());
                    if (vecNotNull != null)
                        checkPart.addGroupAtributes(vecNotNull);
                }
                // skelet atributes check
                IsNullAtributeGroupVector vecSkeletNull = createNullVecAtributes(getSkeletAtributesC2R());
                if (vecSkeletNull != null)
                    checkPart.addGroupAtributes(vecSkeletNull);
                // glued atributes check
                for (Enumeration elements = getGluedRelationsC2R().elements(); elements.hasMoreElements();) {
                    GluedRelationRecord relationRecord = (GluedRelationRecord) elements.nextElement();
                    RelationC2R relationC2R = relationRecord.getRelationC2R();
                    if (relationRecord != gluedRelationRecord) {
                        IsNullAtributeGroupVector vecNull = createNullVecAtributes(relationRecord.getGluedAtributesC2R());
                        if (vecNull != null)
                            checkPart.addGroupAtributes(vecNull);
                    }
                }
                // not glued atributes check
                for (Enumeration elements = getNotGluedRelationsC2R().elements(); elements.hasMoreElements();) {
                    GluedRelationRecord relationRecord = (GluedRelationRecord) elements.nextElement();
                    RelationC2R relationC2R = relationRecord.getRelationC2R();
                    if (relationRecord != gluedRelationRecord) {
                        IsNullAtributeGroupVector vecNull = createNullVecAtributes(relationRecord.getGluedAtributesC2R());
                        if (vecNull != null)
                            checkPart.addGroupAtributes(vecNull);
                    }
                }
                orCheck.addORPartOfCheck(checkPart);
            }
        }

        return orCheck;
    }

    /**
     * @return java.util.Vector
     */
    private Vector getGluedRelationsC2R() {
        return gluedRelationsC2R;
    }

    /**
     * @return java.util.Vector
     */
    private Vector getNotGluedRelationsC2R() {
        return notGluedRelationsC2R;
    }

    /**
     * @return java.util.Vector
     */
    protected Vector getSkeletAtributesC2R() {
        return skeletAtributesC2R;
    }
}
