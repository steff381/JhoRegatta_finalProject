package aad.finalproject.db;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aad.finalproject.jhoregatta.R;

/*
 class containing all data that is manipulated for teh boat lass
 */
public class BoatAdapter extends BaseAdapter{
    Context mContext;
    LayoutInflater inflater;
    private List<Boat> mainDataList = null;

    public BoatAdapter(Context context, List<Boat> mainDataList) {
        mContext = context;
        this.mainDataList = mainDataList;
        inflater = LayoutInflater.from(mContext);
        ArrayList<Boat> arraylist = new ArrayList<Boat>();
        arraylist.addAll(mainDataList);
    }

    static class ViewHolder {
        protected TextView id;
        protected TextView boatName;
        protected TextView boatSailNum;
        protected TextView boatClass;
        protected TextView boatPHRF;
        protected TextView boatVisible;
        protected CheckBox checkBox;

    }


    /**
     * How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        return mainDataList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public Boat getItem(int position) {
        return mainDataList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_list_template_select_boats, null);
            // check box indicating selection
            holder.checkBox = (CheckBox) view.findViewById(R.id.ckboxSelectBoatCheck);
            // fields in each row of the list
            holder.id = (TextView) view.findViewById(R.id.txt_hd_sb_ID); // Not visible in list
            holder.boatPHRF = (TextView) view.findViewById(R.id.txt_hd_sb_PHRF); // Not visible in list
            holder.boatVisible = (TextView) view.findViewById(R.id.txt_hd_sb_Visible); // Not visible in list
            holder.boatClass = (TextView) view.findViewById(R.id.txt_hd_sb_Class);
            holder.boatName = (TextView) view.findViewById(R.id.txt_hd_sb_Name);
            holder.boatSailNum = (TextView) view.findViewById(R.id.txt_hd_sb_SailNum);


            view.setTag(holder);
            view.setTag(R.id.txt_hd_sb_ID, holder.id);
            view.setTag(R.id.txt_hd_sb_PHRF, holder.boatPHRF);
            view.setTag(R.id.txt_hd_sb_Visible, holder.boatVisible);
            view.setTag(R.id.txt_hd_sb_Class, holder.boatClass);
            view.setTag(R.id.txt_hd_sb_Name, holder.boatName);
            view.setTag(R.id.txt_hd_sb_SailNum, holder.boatSailNum);
            view.setTag(R.id.ckboxSelectBoatCheck, holder.checkBox);

            holder.checkBox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton vw, boolean isChecked) {
                            int getPosition = (Integer) vw.getTag();
                            Log.i("BoatAdapter ", "OncheckChanged triggered for " + mainDataList.get(getPosition).getBoatName() + " getPosition is: " + getPosition);
                            mainDataList.get(getPosition).setSelected(vw.isChecked());
                            Log.i("BoatAdapter ", " isSelected is now: " + mainDataList.get(getPosition).isSelected());

                            for (Boat boat : BoatListClass.boatList) {
                                String listBoatId = boat.getId() + "";
                                String mainBoatId = mainDataList.get(getPosition).getId() + "";
                                if (listBoatId.equals(mainBoatId)) {
                                    boat.setSelected(vw.isChecked());
                                    Log.i("BoatAdapter ", " boatList entry: " + boat.getBoatName() + " was selected and changed to: " + vw.isChecked());
                                }
                            }
                        }
                    });

        } else {

            holder = (ViewHolder) view.getTag(); // get instance data for the whole view
        }

        //set instance data to holder
        holder.checkBox.setTag(position);

        holder.id.setText(String.valueOf(mainDataList.get(position).getId())); // pass a long as a string
        holder.boatClass.setText(mainDataList.get(position).getBoatClass());
        holder.boatName.setText(mainDataList.get(position).getBoatName());
        holder.boatSailNum.setText(mainDataList.get(position).getBoatSailNum());

        holder.checkBox.setChecked(mainDataList.get(position).isSelected());

        return view;
    }


}
