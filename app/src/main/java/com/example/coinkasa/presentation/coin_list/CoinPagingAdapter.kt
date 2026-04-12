package com.example.coinkasa.presentation.coin_list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.coinkasa.databinding.ItemCoinBinding
import com.example.coinkasa.domain.model.Coin

class CoinPagingAdapter(
    private val onItemClick: (Coin) -> Unit
) : PagingDataAdapter<Coin, CoinPagingAdapter.CoinViewHolder>(CoinComparator) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = ItemCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        getItem(position)?.let { coin ->
            holder.bind(coin)
        }
    }

    inner class CoinViewHolder(private val binding: ItemCoinBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coin: Coin) {
            binding.apply {
                tvCoinName.text = coin.name
                tvCoinSymbol.text = coin.symbol.uppercase()

                val price = coin.currentPrice
                val change = coin.priceChangePercentage24h

                if (price <= 0.0) {
                    tvCoinPrice.text = "---"
                    tvCoinChange.text = ""
                } else {
                    tvCoinPrice.text = String.format("$%.4f", price)
                    tvCoinChange.text = String.format("%+.2f%%", change)

                    if (change >= 0) {
                        tvCoinChange.setTextColor(Color.parseColor("#00C853"))
                    } else {
                        tvCoinChange.setTextColor(Color.parseColor("#D50000"))
                    }
                }

                ivCoinLogo.load(coin.imageUrl) {
                    crossfade(true)
                }

                root.setOnClickListener {
                    onItemClick(coin)
                }
            }
        }
    }

    object CoinComparator : DiffUtil.ItemCallback<Coin>() {
        override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
            return oldItem == newItem
        }
    }
}