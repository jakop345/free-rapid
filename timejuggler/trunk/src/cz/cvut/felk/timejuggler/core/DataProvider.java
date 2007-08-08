/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.entity.DbElement;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import org.izvin.client.desktop.ui.util.UIBeanEnhancer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import cz.cvut.felk.timejuggler.db.DatabaseException;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private static DataProvider ourInstance = new DataProvider();
    List<Category> categories;
    private ArrayListModel<VCalendar> calendars;

    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
        calendars = new ArrayListModel<VCalendar>();
        categories = Arrays.asList(new Category("Birthday", Color.YELLOW), new Category("Annivesary", Color.BLUE), new Category("Annivesary", Color.BLUE));

        calendars = null; //lazy inicializace
    }


    //utilita, bude pozdeji presunuto, az jestli tohle bude potreba a budu vedet kam ;-)
    private <C> ArrayListModel<C> enhanceToBeans(List<C> list) {
        final ArrayListModel<C> listModel = new ArrayListModel<C>();
        for (C item : list) {
            listModel.add((C) UIBeanEnhancer.enhance(item));
        }
        return listModel;
    }


    public List<Category> getCategories() {
        return categories;
    }

    public synchronized ArrayListModel<VCalendar> getCalendars() {
        String sql = "SELECT * FROM VCalendar";
        TimeJugglerJDBCTemplate<ArrayListModel<VCalendar>> template = new TimeJugglerJDBCTemplate<ArrayListModel<VCalendar>>() {
            protected void handleRow(ResultSet rs) throws SQLException {
            	if (items == null) items = new ArrayListModel<VCalendar>();
                VCalendar cal = new VCalendar();
                cal.setId(Integer.valueOf(rs.getInt("vCalendarID")).intValue());
                cal.setProductId(rs.getString("prodid"));
                cal.setCalendarScale(rs.getString("calscale"));
                cal.setMethod(rs.getString("method"));
                cal.setVersion(rs.getString("version"));
                cal.setName(rs.getString("name"));
                items.add(cal);
            }
        };
        template.executeQuery(sql, null);
        calendars = template.getItems();

        if (calendars == null) {
            //fake data
            List<VCalendar> normalFromDBList = Arrays.asList(new VCalendar("VityCalendar"), new VCalendar("JohnnyCalendar"), new VCalendar("SvatkyCalendar"));
            calendars = enhanceToBeans(normalFromDBList);
        }
        return calendars;
    }

    /**
     * Method saveOrUpdate
     *
     * Ulozi entitu do databaze
     */
    public <C extends DbElement> void saveOrUpdate(C entity) throws DatabaseException {
    	TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
    	entity.saveOrUpdate(template);
    	template.commit();
    }
}
