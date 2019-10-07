package com.vegst.memorio

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_concept_sibling.view.*

class ConceptFamilyAdapter(): RecyclerView.Adapter<ViewHolder>() {

    //private var mOnClickListener: OnClickListener? = null
    private var mOnClickListener: ((Concept.Concept?) -> Unit)? = null
    fun setOnClickListener(listener: (Concept.Concept?) -> Unit) { this.mOnClickListener = listener }
    private var mConceptFamily: ConceptFamily? = null

    fun updateConceptFamily(conceptFamily: ConceptFamily?) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(mConceptFamily, conceptFamily))
        mConceptFamily = conceptFamily
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int) : Int {
        return mConceptFamily?.getMemberType(position) ?: -1
    }

    override fun getItemCount(): Int {
        return mConceptFamily?.getSize() ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val concept = mConceptFamily?.getMember(position)
        holder.mTextName.text = concept?.name ?: ""
        holder.mContent.setOnClickListener {
            mOnClickListener?.invoke(concept)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            ConceptFamily.TYPE_SIBLING -> return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_concept_sibling, parent, false))
            ConceptFamily.TYPE_PARENT -> return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_concept_parent, parent, false))
            ConceptFamily.TYPE_CHILD -> return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_concept_child, parent, false))
            else -> return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_concept_sibling, parent, false))
        }
    }


}
class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val mTextName = view.textName
    val mContent = view.content
}


class DiffCallback(val oldConceptFamily: ConceptFamily?, val newConceptFamily: ConceptFamily?) : DiffUtil.Callback() {

    override fun getOldListSize() : Int {
        return oldConceptFamily?.getSize() ?: 0
    }

    override fun getNewListSize() : Int {
        return newConceptFamily?.getSize() ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean {
        return newConceptFamily?.getMember(newItemPosition)?.id == oldConceptFamily?.getMember(oldItemPosition)?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean {
        return newConceptFamily?.getMember(newItemPosition)?.name == oldConceptFamily?.getMember(oldItemPosition)?.name
    }


}

class ConceptFamily(val siblings: List<Concept.Concept>, val parents: List<Concept.Concept>, val children: List<Concept.Concept>) {

    companion object {
        const val TYPE_SIBLING = 1
        const val TYPE_PARENT = 2
        const val TYPE_CHILD = 3
    }

    fun getSize() : Int { return siblings.size + parents.size + children.size }
    fun getMember(position: Int) : Concept.Concept {
        when (getMemberType(position)) {
            TYPE_CHILD -> return children.get(position-siblings.size-parents.size)
            TYPE_PARENT -> return parents.get(position-siblings.size)
            TYPE_SIBLING -> return siblings.get(position)
            else -> return siblings.get(position)
        }
    }
    fun getMemberType(position: Int) : Int {
        if (position >= siblings.size + parents.size)
            return TYPE_CHILD
        if (position >= siblings.size)
            return TYPE_PARENT
        return TYPE_SIBLING
    }
}