package quokka.jellenberger.ogrocer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack on 1/12/2016.
 */
public class ItemInfo {
    private long itemID;
    private String itemName;
    private List<Double> prices;
    private List<String> stores;
    private int inTab; // 1 if yes, 0 if saved, -1 if deleted

    public ItemInfo(){};
    public ItemInfo(String name, int inTab)
    {
        List<Double> p =  new ArrayList<Double>(); p.add(0.00);
        List<String> s =  new ArrayList<String>(); s.add("location");

        this.itemName = name;
        this.itemID = -1;
        this.prices =p;
        this.stores =s;
        this.inTab = inTab;
        //match rest on network db
    };
    public ItemInfo(long id, String name, List<Double> prices, List<String> stores, int inTab){
        this.itemID = id;
        this.itemName = name;
        this.prices = prices;
        this.stores = stores;
        this.inTab = inTab;
    }

    public void setItemID(long id){
        this.itemID = id;
    }
    public void setItemName(String name){
        this.itemName = name;
    }
    public void setPrices(List<Double> prices){
        this.prices = prices;
    }
    public void setStores(List<String> stores){
        this.stores = stores;
    }
    public void setPrices(String prices){
        List<String> s_prices = Arrays.asList(prices.split("\\s*,\\s*"));
        List<Double> d_prices = new ArrayList<>();
        for(String s : s_prices)
            d_prices.add(Double.parseDouble(s));
        this.prices = d_prices;
    }
    public void setStores(String stores){
        this.stores = Arrays.asList(stores.split("\\s*,\\s*"));
    }

    public long getItemID(){
        return this.itemID;
    }
    public String getItemName(){
        return this.itemName;
    }
    public List<Double> getPrices(){
        return this.prices;
    }
    public List<String> getStores(){
        return this.stores;
    }
    public String s_getPrices(){
        String s_Prices = "";
        for (Double s : this.prices)
            s_Prices += String.valueOf(s) + ", ";
        return s_Prices;
    }
    public String s_getStores(){
        String s_Prices = "";
        for (String s : this.stores)
            s_Prices += s + ", ";
        return s_Prices;
    }
    public int getInTab(){
        return this.inTab;
    }
    public void setInTab(int inTab){
        this.inTab = inTab;
    }

}
