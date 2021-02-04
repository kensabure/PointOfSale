package com.msdevelopers.salepointapp.helpers;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.msdevelopers.salepointapp.fragments.OffersFragment;
import com.msdevelopers.salepointapp.fragments.OrdersFragment;
import com.msdevelopers.salepointapp.fragments.ProductFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter
{

    public TabsAccessorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ProductFragment productFragment = new ProductFragment();
                return productFragment;

            case 1:
                OffersFragment offersFragment = new OffersFragment();
                return offersFragment;

            case 2:
                OrdersFragment ordersFragment = new OrdersFragment();
                return ordersFragment;

            default:
                return null;
        }
    }


    @Override
    public int getCount()
    {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Products";

            case 1:
                return "Offers";

            case 2:
                return "Orders";

            default:
                return null;
        }
    }
}
