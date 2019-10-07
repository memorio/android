package com.vegst.memorio

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.support.v7.view.ActionMode
import android.view.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_concept.fab
import kotlinx.android.synthetic.main.fragment_concept.recyclerView
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.widget.AppCompatImageButton
import android.view.MenuInflater
import android.widget.EditText


class ConceptFragment : Fragment(), ConceptEventListener {

    companion object {
        fun newInstance(id: String): ConceptFragment {
            val fragment = ConceptFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    private var mConceptName: String? = null
    private var mOnConceptClickListener: ((Concept.Concept?) -> Unit)? = null
    fun setOnConceptClickListener(listener: (Concept.Concept?) -> Unit) { this.mOnConceptClickListener = listener }

    private lateinit var mDatabase: FirebaseFirestore
    private var mConceptReference: DocumentReference? = null

    private lateinit var mAdapter: ConceptFamilyAdapter

    private var mEditMode: ActionMode? = null
    private lateinit var mEditModeCallback: ActionMode.Callback


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_concept, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true);

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val id = arguments?.get("id") as? String

        mDatabase = FirebaseFirestore.getInstance()
        mConceptReference = id?.let { mDatabase.collection("concepts").document(it) }
        setupRecyclerView()
        setupActionMode()
    }

    fun setupRecyclerView() {

        mAdapter = ConceptFamilyAdapter()
        mAdapter.setOnClickListener {
            mOnConceptClickListener?.invoke(it)
        }
        mConceptReference?.addSnapshotListener(this)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter

    }

    fun setupActionMode() {
        mEditModeCallback = object: ActionMode.Callback {


            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                mEditMode = null
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.concept_edit_mode, menu)
                val editTitle = (menu.findItem(R.id.main).actionView.findViewById(R.id.editTitle) as? EditText)
                val buttonSave = (menu.findItem(R.id.main).actionView.findViewById(R.id.buttonSave) as? AppCompatImageButton)
                editTitle?.setText(mConceptName)
                editTitle?.hint = mConceptName
                buttonSave?.setOnClickListener {
                    mConceptReference?.update("name", editTitle?.text.toString())?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            mEditMode?.finish()
                        }
                        else {
                            view?.let {
                                Snackbar.make(it, "Could not save", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    //R.id.cancel -> {

                    //}
                }
                return true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                mEditMode = (activity as? AppCompatActivity)?.startSupportActionMode(mEditModeCallback)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onEvent(concept: Concept?) {
        mConceptName = concept?.name
        if (concept != null) {
            mAdapter.updateConceptFamily(ConceptFamily(concept.siblings, concept.parents, concept.children))
            (activity as? AppCompatActivity)?.title = concept.name
        }
    }


}
