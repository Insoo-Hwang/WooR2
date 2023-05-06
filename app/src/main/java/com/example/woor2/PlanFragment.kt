package com.example.woor2

import android.app.Activity
import android.content.*
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentPlanBinding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlanFragment: Fragment() {

    private lateinit var binding: FragmentPlanBinding
    private val viewModel by viewModels<PlanViewModel>()
    private lateinit var adapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanBinding.inflate(inflater, container, false)
        val recyclerView = binding.planrecycleView
        adapter = PlanAdapter(viewModel, requireActivity())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        viewModel.itemsListData.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
        }
        registerForContextMenu(binding.planrecycleView)


        viewModel.itemClickEvent.observe(viewLifecycleOwner){
            val intent = Intent(activity, AddingPlanActivity::class.java)
            intent.putExtra("code", viewModel.items[viewModel.itemClickEvent.value!!].id)
            intent.putExtra("mode", 0)
            startActivity(intent)
        }

        val icon = context?.let { ContextCompat.getDrawable(it, R.drawable.plus) }
        context?.let { ContextCompat.getColor(it, R.color.A) }
            ?.let { icon?.setColorFilter(it, PorterDuff.Mode.SRC_ATOP) }
        binding.plusButton.setImageDrawable(icon)

        binding.plusButton.setOnClickListener {
            val intent = Intent(activity, AddingPlanActivity::class.java)
            intent.putExtra("location", "")
            intent.putExtra("code", -1)
            intent.putExtra("mode", 1)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /*override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.plan_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val db : FirebaseFirestore = Firebase.firestore
        val schedulesRef = db.collection("schedules")
        when(item.itemId){
            R.id.share->{
                //onDynamicLinkClick(requireActivity(),viewModel.items[viewModel.itemLongClick].id)
                share(viewModel.items[viewModel.itemLongClick].id)
            }
            R.id.shareQR -> {
                val intent = Intent(requireActivity(), QrActivity::class.java)
                intent.putExtra("key", viewModel.items[viewModel.itemLongClick].id)
                startActivity(intent)
            }
            R.id.delete -> {
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                schedulesRef.document(viewModel.items[viewModel.itemLongClick].id).delete()
                viewModel.deleteItem(viewModel.itemLongClick)
            }
            else -> return false
        }
        return true
    }*/

    /*private fun getDeepLink(key: String): Uri {
        return Uri.parse("https://woorii.com/${key}")
    }

    fun onDynamicLinkClick(activity: Activity, key: String = "tV8u") {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(getDeepLink(key))
            .setDynamicLinkDomain("woorii.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(activity.packageName).setMinimumVersion(1).build())
            .buildShortDynamicLink()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val shortLink: Uri = task.result.shortLink!!
                    System.out.println("#####################")
                    System.out.println(shortLink)
                    try {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                        sendIntent.type = "text/plain"
                        activity.startActivity(Intent.createChooser(sendIntent, "Share"))
                    } catch (ignored: ActivityNotFoundException) {

                    }
                }
            }
    }*/

    /*fun share(code : String) {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, code)
            sendIntent.type = "text/plain"
            activity?.startActivity(Intent.createChooser(sendIntent, "Share"))
        } catch (ignored: ActivityNotFoundException) {

        }
    }*/
}

