package com.example.coinkasa.presentation.portfolio

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.coinkasa.databinding.ItemPortfolioBinding
import com.example.coinkasa.domain.model.PortfolioItem
import java.util.Locale

class PortfolioAdapter(
    private val onItemClick: (PortfolioItem) -> Unit
) : ListAdapter<PortfolioItem, PortfolioAdapter.PortfolioViewHolder>(PortfolioComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val binding = ItemPortfolioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PortfolioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class PortfolioViewHolder(private val binding: ItemPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PortfolioItem) {
            binding.apply {
                tvCoinName.text = item.coinName

                val formattedAmount = if (item.amount % 1.0 == 0.0) {
                    String.format(Locale.US, "%.0f", item.amount)
                } else {
                    item.amount.toString()
                }
                tvCoinSymbol.text = "$formattedAmount ${item.coinSymbol.uppercase()}"

                tvHoldingsValue.text = String.format(Locale.US, "$%.2f", item.totalValue)

                val profitLossSign = if (item.profitLoss >= 0) "+" else ""
                val profitLossText = String.format(
                    Locale.US,
                    "%s$%.2f (%s%.2f%%)",
                    profitLossSign, item.profitLoss,
                    profitLossSign, item.profitLossPercentage
                )
                tvProfitLoss.text = profitLossText

                if (item.profitLoss >= 0) {
                    tvProfitLoss.setTextColor(Color.parseColor("#00C853"))
                } else {
                    tvProfitLoss.setTextColor(Color.parseColor("#D50000"))
                }

                ivCoinLogo.load(item.imageUrl) {
                    crossfade(true)
                }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    object PortfolioComparator : DiffUtil.ItemCallback<PortfolioItem>() {
        override fun areItemsTheSame(oldItem: PortfolioItem, newItem: PortfolioItem): Boolean {
            return oldItem.coinId == newItem.coinId
        }

        override fun areContentsTheSame(oldItem: PortfolioItem, newItem: PortfolioItem): Boolean {
            return oldItem == newItem
        }
    }
}