package cz.felk.cvut.erm.fileimport.ermver4;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the cz.felk.cvut.erm.fileimport.ermver4 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Position_QNAME = new QName("", "position");
    private final static QName _Arbitrary_QNAME = new QName("", "arbitrary");
    private final static QName _Scale_QNAME = new QName("", "scale");
    private final static QName _Unique_QNAME = new QName("", "unique");
    private final static QName _Entity_QNAME = new QName("", "entity");
    private final static QName _Glue_QNAME = new QName("", "glue");
    private final static QName _Id_QNAME = new QName("", "id");
    private final static QName _Schema_QNAME = new QName("", "schema");
    private final static QName _Child_QNAME = new QName("", "child");
    private final static QName _Datatypedef_QNAME = new QName("", "datatypedef");
    private final static QName _Height_QNAME = new QName("", "height");
    private final static QName _Itemname_QNAME = new QName("", "itemname");
    private final static QName _Atr_QNAME = new QName("", "atr");
    private final static QName _Primary_QNAME = new QName("", "primary");
    private final static QName _Name_QNAME = new QName("", "name");
    private final static QName _Left_QNAME = new QName("", "left");
    private final static QName _Notation_QNAME = new QName("", "notation");
    private final static QName _Top_QNAME = new QName("", "top");
    private final static QName _Width_QNAME = new QName("", "width");
    private final static QName _Constraints_QNAME = new QName("", "constraints");
    private final static QName _Relation_QNAME = new QName("", "relation");
    private final static QName _Parent_QNAME = new QName("", "parent");
    private final static QName _Usertype_QNAME = new QName("", "usertype");
    private final static QName _Ent_QNAME = new QName("", "ent");
    private final static QName _Uniq_QNAME = new QName("", "uniq");
    private final static QName _Strong_QNAME = new QName("", "strong");
    private final static QName _Atribute_QNAME = new QName("", "atribute");
    private final static QName _Item_QNAME = new QName("", "item");
    private final static QName _Typename_QNAME = new QName("", "typename");
    private final static QName _Multi_QNAME = new QName("", "multi");
    private final static QName _Rel_QNAME = new QName("", "rel");
    private final static QName _Datatype_QNAME = new QName("", "datatype");
    private final static QName _Comment_QNAME = new QName("", "comment");
    private final static QName _Cardinality_QNAME = new QName("", "cardinality");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cz.felk.cvut.erm.fileimport.ermver4
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BindItem }
     */
    public BindItem createBindItem() {
        return new BindItem();
    }

    /**
     * Create an instance of {@link BindEntity }
     */
    public BindEntity createBindEntity() {
        return new BindEntity();
    }

    /**
     * Create an instance of {@link BindCardinality }
     */
    public BindCardinality createBindCardinality() {
        return new BindCardinality();
    }

    /**
     * Create an instance of {@link BindUserType }
     */
    public BindUserType createBindUserType() {
        return new BindUserType();
    }

    /**
     * Create an instance of {@link BindUnique }
     */
    public BindUnique createBindUnique() {
        return new BindUnique();
    }

    /**
     * Create an instance of {@link BindAttribute }
     */
    public BindAttribute createBindAttribute() {
        return new BindAttribute();
    }

    /**
     * Create an instance of {@link BindStrong }
     */
    public BindStrong createBindStrong() {
        return new BindStrong();
    }

    /**
     * Create an instance of {@link BindDatatypeDef }
     */
    public BindDatatypeDef createBindDatatypeDef() {
        return new BindDatatypeDef();
    }

    /**
     * Create an instance of {@link BindRelation }
     */
    public BindRelation createBindRelation() {
        return new BindRelation();
    }

    /**
     * Create an instance of {@link BindSchema }
     */
    public BindSchema createBindSchema() {
        return new BindSchema();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "position")
    public JAXBElement<Integer> createPosition(Integer value) {
        return new JAXBElement<Integer>(_Position_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "arbitrary")
    public JAXBElement<Boolean> createArbitrary(Boolean value) {
        return new JAXBElement<Boolean>(_Arbitrary_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "scale")
    public JAXBElement<Float> createScale(Float value) {
        return new JAXBElement<Float>(_Scale_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindUnique }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "unique")
    public JAXBElement<BindUnique> createUnique(BindUnique value) {
        return new JAXBElement<BindUnique>(_Unique_QNAME, BindUnique.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindEntity }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "entity")
    public JAXBElement<BindEntity> createEntity(BindEntity value) {
        return new JAXBElement<BindEntity>(_Entity_QNAME, BindEntity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "glue")
    public JAXBElement<Boolean> createGlue(Boolean value) {
        return new JAXBElement<Boolean>(_Glue_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "id")
    public JAXBElement<Integer> createId(Integer value) {
        return new JAXBElement<Integer>(_Id_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindSchema }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "schema")
    public JAXBElement<BindSchema> createSchema(BindSchema value) {
        return new JAXBElement<BindSchema>(_Schema_QNAME, BindSchema.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "child")
    public JAXBElement<Integer> createChild(Integer value) {
        return new JAXBElement<Integer>(_Child_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindDatatypeDef }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "datatypedef")
    public JAXBElement<BindDatatypeDef> createDatatypedef(BindDatatypeDef value) {
        return new JAXBElement<BindDatatypeDef>(_Datatypedef_QNAME, BindDatatypeDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "height")
    public JAXBElement<Integer> createHeight(Integer value) {
        return new JAXBElement<Integer>(_Height_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "itemname")
    public JAXBElement<String> createItemname(String value) {
        return new JAXBElement<String>(_Itemname_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "atr")
    public JAXBElement<Integer> createAtr(Integer value) {
        return new JAXBElement<Integer>(_Atr_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "primary")
    public JAXBElement<Boolean> createPrimary(Boolean value) {
        return new JAXBElement<Boolean>(_Primary_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "left")
    public JAXBElement<Integer> createLeft(Integer value) {
        return new JAXBElement<Integer>(_Left_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "notation")
    public JAXBElement<Integer> createNotation(Integer value) {
        return new JAXBElement<Integer>(_Notation_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "top")
    public JAXBElement<Integer> createTop(Integer value) {
        return new JAXBElement<Integer>(_Top_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "width")
    public JAXBElement<Integer> createWidth(Integer value) {
        return new JAXBElement<Integer>(_Width_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "constraints")
    public JAXBElement<String> createConstraints(String value) {
        return new JAXBElement<String>(_Constraints_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindRelation }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "relation")
    public JAXBElement<BindRelation> createRelation(BindRelation value) {
        return new JAXBElement<BindRelation>(_Relation_QNAME, BindRelation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "parent")
    public JAXBElement<Integer> createParent(Integer value) {
        return new JAXBElement<Integer>(_Parent_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindUserType }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "usertype")
    public JAXBElement<BindUserType> createUsertype(BindUserType value) {
        return new JAXBElement<BindUserType>(_Usertype_QNAME, BindUserType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "ent")
    public JAXBElement<Integer> createEnt(Integer value) {
        return new JAXBElement<Integer>(_Ent_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "uniq")
    public JAXBElement<Boolean> createUniq(Boolean value) {
        return new JAXBElement<Boolean>(_Uniq_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindStrong }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "strong")
    public JAXBElement<BindStrong> createStrong(BindStrong value) {
        return new JAXBElement<BindStrong>(_Strong_QNAME, BindStrong.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindAttribute }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "atribute")
    public JAXBElement<BindAttribute> createAtribute(BindAttribute value) {
        return new JAXBElement<BindAttribute>(_Atribute_QNAME, BindAttribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindItem }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "item")
    public JAXBElement<BindItem> createItem(BindItem value) {
        return new JAXBElement<BindItem>(_Item_QNAME, BindItem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "typename")
    public JAXBElement<String> createTypename(String value) {
        return new JAXBElement<String>(_Typename_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "multi")
    public JAXBElement<Boolean> createMulti(Boolean value) {
        return new JAXBElement<Boolean>(_Multi_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "rel")
    public JAXBElement<Integer> createRel(Integer value) {
        return new JAXBElement<Integer>(_Rel_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "datatype")
    public JAXBElement<String> createDatatype(String value) {
        return new JAXBElement<String>(_Datatype_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindCardinality }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "cardinality")
    public JAXBElement<BindCardinality> createCardinality(BindCardinality value) {
        return new JAXBElement<BindCardinality>(_Cardinality_QNAME, BindCardinality.class, null, value);
    }

}
