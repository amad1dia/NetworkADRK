package fr.istic.mob.network.networkadrk.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import fr.istic.mob.network.networkadrk.R
import fr.istic.mob.network.networkadrk.ui.CustomView

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)

        return CustomView(mContext)
    }

    private fun drawObject(v: View): Boolean {
        Log.d(TAG, "drawObject: ")
        var drawView = CustomView(mContext)


        return true
    }
}