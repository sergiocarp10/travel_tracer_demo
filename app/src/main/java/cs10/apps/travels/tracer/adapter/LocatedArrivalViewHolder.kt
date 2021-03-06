package cs10.apps.travels.tracer.adapter

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cs10.apps.travels.tracer.R
import cs10.apps.travels.tracer.Utils
import cs10.apps.travels.tracer.databinding.ItemArrivalBinding
import cs10.apps.travels.tracer.data.generator.Station
import cs10.apps.travels.tracer.model.Viaje
import cs10.apps.travels.tracer.model.roca.ArriboTren
import cs10.apps.travels.tracer.ui.stops.ETA_Switcher

class LocatedArrivalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemArrivalBinding.bind(view)
    private val etaSwitcher = ETA_Switcher()

    fun render(viaje: Viaje, top: Boolean, onClickListener: (ArriboTren) -> Unit, onDepartCallback: (Viaje) -> Unit) {

        // ramal
        if (viaje is ArriboTren) renderRamal(viaje)
        else binding.tvName.text = if (viaje.ramal == null) "" else "Ramal " + viaje.ramal

        // destination
        binding.tvStartCount.text = if (viaje.endHour != null && viaje.endMinute != null){
            binding.root.context.getString(
                R.string.destination_full,
                viaje.nombrePdaFin,
                Utils.hourFormat(viaje.endHour!!, viaje.endMinute!!)
            )
        } else {
            binding.root.context.getString(
                R.string.destination,
                viaje.nombrePdaFin
            )
        }

        // icon
        val icon = if (viaje.tipo == 0) R.drawable.ic_bus
        else R.drawable.ic_train
        binding.ivType.setImageDrawable(AppCompatResources.getDrawable(binding.root.context, icon))

        // bg color
        val color = if (viaje.tipo == 1 && !viaje.ramal.isNullOrEmpty() && viaje.ramal!!.contains("Directo")) R.color.bus_159
        else Utils.colorFor(viaje.linea)
        binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, color))

        // line sublabel
        binding.tvLine.text = if (viaje.linea == null) "Roca"
        else viaje.linea.toString()

        // ETA animation
        if (top && viaje is ArriboTren){
            etaSwitcher.setItem(viaje)
            etaSwitcher.setTvSwitcher(binding.tvSwitcher)
            etaSwitcher.setCallback(onDepartCallback)
            etaSwitcher.startAnimation()

            binding.tvSwitcher.isVisible = true
            binding.tvLocation.isVisible = false
        } else {
            etaSwitcher.stop()
            binding.tvLocation.text = binding.root.context.getString(
                R.string.arrival_time,
                Utils.hourFormat(viaje.startHour, viaje.startMinute)
            )

            binding.tvLocation.isVisible = true
            binding.tvSwitcher.isVisible = false
        }

        // click listener for this item
        if (viaje is ArriboTren) binding.root.setOnClickListener { onClickListener(viaje) }
    }

    private fun renderRamal(arriboTren: ArriboTren) {

        if (arriboTren.nombrePdaFin == arriboTren.nombrePdaInicio) {
            val bosques: Boolean = arriboTren.isFutureStation(Station.BOSQUES)
            val temperley: Boolean = arriboTren.isFutureStation(Station.TEMPERLEY)
            val quilmes: Boolean = arriboTren.isFutureStation(Station.QUILMES)
            val platanos: Boolean = arriboTren.isFutureStation(Station.PLATANOS)

            if (!bosques) {
                if (temperley) arriboTren.ramal = "Via Temperley"
                else if (quilmes && !platanos) arriboTren.ramal = "Via Quilmes"
            }
        }

        binding.tvName.text = arriboTren.ramal
    }
}