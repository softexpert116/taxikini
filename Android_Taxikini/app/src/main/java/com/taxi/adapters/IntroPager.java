package com.taxi.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.taxi.common.Utils.Util;
import com.taxi.kini.R;

public class IntroPager extends PagerAdapter {
    private Context context;
    public IntroPager(Context context) {
        this.context = context;
    }
    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.intro_item, null);
        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageDrawable(context.getResources().getDrawable(getImageAt(position)));
        container.addView(view);
        return view;
    }
    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }
    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {

        if (Util.currentUser.type.equalsIgnoreCase(Util.DRIVER)) return 11;
        else return 12;
    }
    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
    private int getImageAt(int position) {

        if (Util.currentUser.type.equalsIgnoreCase(Util.DRIVER)) {
            switch (position) {
                case 0:
                    return R.drawable.content0;
                case 1:
                    return R.drawable.dcontent1;
                case 2:
                    return R.drawable.dcontent2;
                case 3:
                    return R.drawable.dcontent3;
                case 4:
                    return R.drawable.dcontent4;
                case 5:
                    return R.drawable.dcontent5;
                case 6:
                    return R.drawable.dcontent6;
                case 7:
                    return R.drawable.dcontent7;
                case 8:
                    return R.drawable.dcontent8;
                case 9:
                    return R.drawable.dcontent9;
                case 10:
                    return R.drawable.dcontent10;
                default:
                    return R.drawable.content0;
            }
        }
        else {
            switch (position) {
                case 0:
                    return R.drawable.content0;
                case 1:
                    return R.drawable.content1;
                case 2:
                    return R.drawable.content2;
                case 3:
                    return R.drawable.content3;
                case 4:
                    return R.drawable.content4;
                case 5:
                    return R.drawable.content5;
                case 6:
                    return R.drawable.content6;
                case 7:
                    return R.drawable.content7;
                case 8:
                    return R.drawable.content8;
                case 9:
                    return R.drawable.content9;
                case 10:
                    return R.drawable.content10;
                case 11:
                    return R.drawable.content11;

                default:
                    return R.drawable.content0;
            }
        }

    }
}
