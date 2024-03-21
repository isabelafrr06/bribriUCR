package com.example.bribriucr.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bribriucr.databinding.FragmentHomeBinding
import com.example.bribriucr.ui.TopicCard
import com.example.bribriucr.ui.TopicCardAdapter


class HomeFragment(private val homeModel: HomeModel, context: Context) : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val listaTemas = ArrayList<TopicCard>()
    private val adapter = TopicCardAdapter(listaTemas, context)
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerList.layoutManager = LinearLayoutManager(context)
        binding.recyclerList.adapter = adapter
        fetchTopicCards()

        return binding.root
    }

    /**
     * Fetches topic cards and updates the UI.
     */
    private fun fetchTopicCards() {
        val topics = homeModel.fetchTopicCards()
        listaTemas.addAll(topics)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}