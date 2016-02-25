package quokka.jellenberger.ogrocer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jacka on 2/20/2016.
 */
public class ItineraryObject implements Parcelable {
    //common
    public String type;
    public String innerString;
    //directions
    public int drawableResourceID;
    public int stepNumber;
    //ingredient
    public boolean checked;
    public int ingredientID;
    public String store;

    public ItineraryObject(String type){
        this.type = type;
        this.innerString = "";

        this.drawableResourceID = -1;
        this.stepNumber = -1;

        this.checked = false;
        this.ingredientID = -1;
        this.store="";
    }
    //direction contructor
    public ItineraryObject(String type, String innerString, int drawableResourceID, int stepNumber, String store){
        this.type = type;
        this.innerString = innerString;

        this.drawableResourceID = drawableResourceID;
        this.stepNumber = stepNumber;

        this.checked = false;
        this.ingredientID = -1;

        this.store = store;
    }
    // ingredient constructor
    public ItineraryObject(String type, String innerString, boolean checked, int ingredientID, String store){
        this.type = type;
        this.innerString = innerString;

        this.drawableResourceID = -1;
        this.stepNumber = -1;

        this.checked = checked;
        this.ingredientID = ingredientID;

        this.store = store;
    }

    protected ItineraryObject(Parcel in) {
        type = in.readString();
        innerString = in.readString();
        drawableResourceID = in.readInt();
        stepNumber = in.readInt();
        checked = in.readByte() != 0;
        ingredientID = in.readInt();
        store = in.readString();
    }

    public static final Creator<ItineraryObject> CREATOR = new Creator<ItineraryObject>() {
        @Override
        public ItineraryObject createFromParcel(Parcel in) {
            return new ItineraryObject(in);
        }

        @Override
        public ItineraryObject[] newArray(int size) {
            return new ItineraryObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(innerString);
        dest.writeInt(drawableResourceID);
        dest.writeInt(stepNumber);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeInt(ingredientID);
        dest.writeString(store);
    }

    public void readFromParcel(Parcel parcel){
        this.type = parcel.readString();
        this.innerString = parcel.readString();
        this.drawableResourceID = parcel.readInt();
        this.stepNumber = parcel.readInt();
        this.checked = parcel.readByte() == 0 ? false : true;
        this.ingredientID = parcel.readInt();
        this.store= parcel.readString();
    }
}
