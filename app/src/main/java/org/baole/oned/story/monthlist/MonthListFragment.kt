package org.baole.oned.story.monthlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import org.baole.oned.MainActivity
import org.baole.oned.databinding.StoryMonthlistFragmentBinding
import org.baole.oned.databinding.StoryPagerFragmentBinding
import org.baole.oned.model.Story
import org.baole.oned.story.StoryFragment
import org.baole.oned.util.FirestoreUtil

class MonthListFragment: Fragment() {
    private lateinit var mBinding: StoryMonthlistFragmentBinding
    private var mRegistration: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = StoryMonthlistFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MonthListAdapter(this)
        mBinding.viewPager.adapter = adapter
        mBinding.viewPager.currentItem = adapter.itemCount - 1

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        mRegistration = FirestoreUtil.story(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance().currentUser).limit(1)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (isAdded) {
                        val dataSize = querySnapshot?.documents?.size ?: 0
                        (activity as MainActivity).showEmptyView(dataSize <= 0)
                    }
        }
    }

    companion object {
        const val DAY = "day"
        const val IS_TODAY = "is_day"
    }

}