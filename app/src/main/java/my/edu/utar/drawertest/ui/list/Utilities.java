package my.edu.utar.drawertest.ui.list;

import java.util.ArrayList;

public class Utilities {

    public static ArrayList<ListObject> populateFirstList() {
        ArrayList<ListObject> mFirstList = new ArrayList<ListObject>();

        return mFirstList;
    }

    public static ArrayList<ListObject> addList(ArrayList<ListObject> mlist, String title, String description) {
        ListObject mListObject = new ListObject(description, title);
        mlist.add(mListObject);

        return mlist;
    }

    public static ArrayList<ListObject> populateSecondList() {
        ArrayList<ListObject> mSecondList = new ArrayList<ListObject>();

        for (int i = 0; i < 5; i++) {
            ListObject mListObject = new ListObject("Title of second list " + i, "Description here " + i);
            mSecondList.add(mListObject);
        }

        return mSecondList;
    }
}
