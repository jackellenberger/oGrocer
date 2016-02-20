package quokka.jellenberger.ogrocer;

/**
 * Created by jacka on 2/20/2016.
 */
public class ItineraryObject {
    //common
    public String type;
    public String innerString;
    //directions
    public int drawableResourceID;
    public int stepNumber;
    //ingredient
    public boolean checked;
    public int ingredientID;

    public ItineraryObject(String type){
        this.type = type;
        this.innerString = "";

        this.drawableResourceID = -1;
        this.stepNumber = -1;

        this.checked = false;
        this.ingredientID = -1;
    }
    public ItineraryObject(String type, String innerString, int drawableResourceID, int stepNumber){
        this.type = type;
        this.innerString = innerString;

        this.drawableResourceID = drawableResourceID;
        this.stepNumber = stepNumber;

        this.checked = false;
        this.ingredientID = -1;
    }
    public ItineraryObject(String type, String innerString, boolean checked, int ingredientID){
        this.type = type;
        this.innerString = innerString;

        this.drawableResourceID = -1;
        this.stepNumber = -1;

        this.checked = checked;
        this.ingredientID = ingredientID;
    }
}
