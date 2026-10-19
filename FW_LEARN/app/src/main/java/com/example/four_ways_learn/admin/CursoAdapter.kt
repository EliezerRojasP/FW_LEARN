package com.example.four_ways_learn.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.model.CursoAdmin

class CursoAdapter(
    private val alEditar: (CursoAdmin) -> Unit
) : RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {
    private val cursos = mutableListOf<CursoAdmin>()

    fun actualizar(nuevos: List<CursoAdmin>) {
        val anteriores = cursos.size
        cursos.clear()
        if (anteriores > 0) notifyItemRangeRemoved(0, anteriores)
        cursos.addAll(nuevos)
        if (cursos.isNotEmpty()) notifyItemRangeInserted(0, cursos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val binding = ItemAdminRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CursoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        holder.vincular(cursos[position])
    }

    override fun getItemCount(): Int = cursos.size

    inner class CursoViewHolder(
        private val binding: ItemAdminRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun vincular(curso: CursoAdmin) {
            val contexto = binding.root.context
            binding.avatar.text = curso.nombre.take(1).uppercase()
            binding.tituloRegistro.text = curso.nombre
            binding.detalleRegistro.text = curso.descripcion?.ifBlank {
                contexto.getString(R.string.accion_editar)
            } ?: contexto.getString(R.string.accion_editar)
            binding.detalleRegistro.maxLines = 2
            binding.estadoRegistro.setText(
                if (curso.estado == 1) R.string.estado_activo else R.string.estado_inactivo
            )
            binding.estadoRegistro.setTextColor(
                ContextCompat.getColor(
                    contexto,
                    if (curso.estado == 1) R.color.fw_verde else R.color.fw_apagado
                )
            )
            binding.root.setOnClickListener { alEditar(curso) }
        }
    }
}
