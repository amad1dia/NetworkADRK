package fr.istic.mob.network.networkadrk.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import fr.istic.mob.network.networkadrk.R
import fr.istic.mob.network.networkadrk.ui.CustomView
import kotlin.random.Random

class HomeFragment : Fragment(), View.OnLongClickListener, View.OnTouchListener {

    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"
    private lateinit var mContext: Context
    private lateinit var mCustomView: CustomView

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

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.setOnLongClickListener(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCustomView = view.findViewById(R.id.network)
    }


    override fun onLongClick(v: View?): Boolean {
        mCustomView.drawRect()
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        mCustomView.connectObject(event)
        return false
    }
}