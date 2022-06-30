package com.florinstroe.toiletlocator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.models.Review
import com.florinstroe.toiletlocator.databinding.FragmentReviewBinding
import com.florinstroe.toiletlocator.viewmodels.ReviewViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private val toiletViewModel: ToiletViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            binding.submitButton.isEnabled = true
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE

                reviewViewModel.review = Review(
                    binding.ratingBar.rating.toInt(),
                    binding.messageTextInputEditText.text.toString(),
                    toiletViewModel.selectedToilet?.id
                )
                reviewViewModel.addReview()

                binding.progressBar.visibility = View.INVISIBLE
                Snackbar.make(view, getString(R.string.review_successfully_submitted), Snackbar.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            }
        }
    }
}