package com.example.agrichime.agrichime.view.yojna

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.agrichime.R
import com.example.agrichime.agrichime.adapter.YojnaAdapter
import com.example.agrichime.agrichime.utilities.CellClickListener
import com.example.agrichime.agrichime.viewmodel.YojnaViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YojnaListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YojnaListFragment : Fragment(), CellClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: YojnaViewModel
    lateinit var Adapter: YojnaAdapter
    lateinit var yojnaFragment: YojnaFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        viewModel = ViewModelProvider(requireActivity())
//            .get<YojnaViewModel>(YojnaViewModel::class.java)
//
//        viewModel.getAllYojna("yojnas")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        viewModel.message3.observe(viewLifecycleOwner, Observer {
//
//            Log.d("Art All Data", it[0].data.toString())
//            val view = inflater.inflate(R.layout.fragment_yojna_list, container, false)
//
//            Adapter = YojnaAdapter(requireActivity().applicationContext, it, this)
//            val rcyclr_yojnaList = view.findViewById<RecyclerView>(R.id.rcyclr_yojnaList)
//
//            rcyclr_yojnaList.adapter = Adapter
//            rcyclr_yojnaList.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
//
//        })
        // Inflate the layout for this fragment



        // Find views by ID




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Krishi Yojna"
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YojnaListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val RazorPayActivity: Unit
        get() = TODO("Not yet implemented")

    override fun onCellClickListener(name: String) {
        yojnaFragment = YojnaFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        yojnaFragment.setArguments(bundle)
        val transaction = requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, yojnaFragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("yojnaListFrag")
            .commit()
    }

}

