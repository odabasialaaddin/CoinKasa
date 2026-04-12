package com.example.coinkasa.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected abstract val binding: VB
    protected abstract val viewModel: VM
    protected abstract fun getLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutRes = getLayoutRes()
        return if (layoutRes != 0) {
            inflater.inflate(layoutRes, container, false)
        } else {
            try {
                binding.root
            } catch (e: IllegalStateException) {
                null
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    protected abstract fun setupViews()

    protected abstract fun observeData()

    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    protected open fun showLoading() {
    }

    protected open fun hideLoading() {
    }

    protected open fun showError(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }
}