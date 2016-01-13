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

    public ItemInfo(){};
    public ItemInfo(long id, String name, List<Double> prices, List<String> stores){
        this.itemID = id;
        this.itemName = name;
        this.prices = prices;
        this.stores = stores;
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

}
