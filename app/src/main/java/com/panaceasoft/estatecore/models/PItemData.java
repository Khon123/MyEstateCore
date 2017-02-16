package com.panaceasoft.estatecore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 8/2/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PItemData implements Parcelable {

    public int id;

    public int cat_id;

    public int city_id;

    public int agent_id;

    public String name;

    public String description;

    public String address;

    public String lat;

    public String lng;

    public String search_tag;

    public int is_published;

    public String added;

    public String updated;

    public String like_count;

    public String review_count;

    public String inquiries_count;

    public String total_rooms;

    public String bed_rooms;

    public String bath_rooms;

    public String price_min;

    public String price_max;

    public String sq_meter_min;

    public String sq_meter_max;

    public String agent_name;

    public String agent_description;

    public String agent_phone;

    public String agent_email;

    public String agent_photo;

    public int for_sell;

    public int for_rent;

    public String currency_symbol;

    public String currency_short_form;

    public ArrayList<PReviewData> reviews;

    public ArrayList<PImageData> images;

    protected PItemData(Parcel in) {
        id = in.readInt();
        cat_id = in.readInt();
        city_id = in.readInt();
        agent_id = in.readInt();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        search_tag = in.readString();
        is_published = in.readInt();
        added = in.readString();
        updated = in.readString();
        like_count = in.readString();
        review_count = in.readString();
        inquiries_count = in.readString();
        total_rooms = in.readString();
        bed_rooms = in.readString();
        bath_rooms = in.readString();
        price_min = in.readString();
        price_max = in.readString();
        sq_meter_max = in.readString();
        sq_meter_min = in.readString();
        for_sell = in.readInt();
        for_rent = in.readInt();
        agent_name = in.readString();
        agent_description = in.readString();
        agent_phone = in.readString();
        agent_email = in.readString();
        agent_photo = in.readString();
        currency_symbol = in.readString();
        currency_short_form = in.readString();

        if (in.readByte() == 0x01) {
            reviews = new ArrayList<PReviewData>();
            in.readList(reviews, PReviewData.class.getClassLoader());
        } else {
            reviews = null;
        }
        if (in.readByte() == 0x01) {
            images = new ArrayList<PImageData>();
            in.readList(images, PImageData.class.getClassLoader());
        } else {
            images = null;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cat_id);
        dest.writeInt(city_id);
        dest.writeInt(agent_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(search_tag);
        dest.writeInt(is_published);
        dest.writeString(added);
        dest.writeString(updated);
        dest.writeString(like_count);
        dest.writeString(review_count);
        dest.writeString(inquiries_count);
        dest.writeString(total_rooms);
        dest.writeString(bed_rooms);
        dest.writeString(bath_rooms);
        dest.writeString(price_min);
        dest.writeString(price_max);
        dest.writeString(sq_meter_min);
        dest.writeString(sq_meter_max);
        dest.writeInt(for_sell);
        dest.writeInt(for_rent);
        dest.writeString(agent_name);
        dest.writeString(agent_description);
        dest.writeString(agent_phone);
        dest.writeString(agent_email);
        dest.writeString(agent_photo);
        dest.writeString(currency_symbol);
        dest.writeString(currency_short_form);

        if (reviews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviews);
        }
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PItemData> CREATOR = new Parcelable.Creator<PItemData>() {
        @Override
        public PItemData createFromParcel(Parcel in) {
            return new PItemData(in);
        }

        @Override
        public PItemData[] newArray(int size) {
            return new PItemData[size];
        }
    };
}