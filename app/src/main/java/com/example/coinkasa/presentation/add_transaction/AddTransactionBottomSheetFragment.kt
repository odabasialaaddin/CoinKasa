package com.example.coinkasa.presentation.add_transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.coinkasa.databinding.BottomSheetAddTransactionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTransactionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels()
    private var selectedDateMillis: Long = System.currentTimeMillis()

    private var coinId: String = ""
    private var coinName: String = ""
    private var coinSymbol: String = ""

    companion object {
        fun newInstance(coinId: String, coinName: String, coinSymbol: String): AddTransactionBottomSheetFragment {
            val args = Bundle().apply {
                putString("coinId", coinId)
                putString("coinName", coinName)
                putString("coinSymbol", coinSymbol)
            }
            val fragment = AddTransactionBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            coinId = it.getString("coinId", "")
            coinName = it.getString("coinName", "")
            coinSymbol = it.getString("coinSymbol", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupDateInitialValue()
        setupListeners()
        observeUiEvents()
    }

    private fun setupUI() {
        if (coinName.isNotEmpty()) {
            binding.tvTitle.text = "$coinName İşlemi Ekle"
        }
    }

    private fun setupDateInitialValue() {
        updateDateText(selectedDateMillis)
    }

    private fun setupListeners() {
        binding.tvDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            val transactionType = if (binding.btnBuy.isChecked) "BUY" else "SELL"
            viewModel.saveTransaction(
                coinId = coinId,
                coinName = coinName,
                coinSymbol = coinSymbol,
                transactionType = transactionType,
                amountStr = binding.etAmount.text.toString(),
                priceStr = binding.etPrice.text.toString(),
                exchangeName = binding.etExchange.text.toString(),
                dateMillis = selectedDateMillis
            )
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Tarih Seç")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDateMillis = selection
            updateDateText(selection)
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun updateDateText(millis: Long) {
        val localeTr = Locale.forLanguageTag("tr-TR")
        val formatter = SimpleDateFormat("dd MMMM yyyy", localeTr)
        binding.tvDate.text = formatter.format(Calendar.getInstance().apply { timeInMillis = millis }.time)
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is AddTransactionViewModel.UiEvent.SaveSuccess -> {
                            Toast.makeText(context, "İşlem başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        is AddTransactionViewModel.UiEvent.ShowError -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}