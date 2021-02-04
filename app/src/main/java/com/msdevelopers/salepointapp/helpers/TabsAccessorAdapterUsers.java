package com.msdevelopers.salepointapp.helpers;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.msdevelopers.salepointapp.fragments.FavouritesFragment;
import com.msdevelopers.salepointapp.fragments.UserOrderFragment;
import com.msdevelopers.salepointapp.fragments.VendorsFragment;

public class TabsAccessorAdapterUsers extends FragmentPagerAdapter
{

    public TabsAccessorAdapterUsers(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                VendorsFragment vendorsFragment = new VendorsFragment();
                return vendorsFragment;

            case 1:
                UserOrderFragment userOrderFragment = new UserOrderFragment();
                return userOrderFragment;

            case 2:
                FavouritesFragment favouritesFragment = new FavouritesFragment();
                return favouritesFragment;

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
                return "Vendors";

            case 1:
                return "My Orders";

            case 2:
                return "Favourites";

            default:
                return null;
        }
    }
}
