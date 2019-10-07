package com.vegst.memorio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

interface ConceptEventListener: EventListener<DocumentSnapshot> {

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e == null && snapshot != null && snapshot.exists()) {
            val siblings = ArrayList<Concept.Concept>()
            val parents = ArrayList<Concept.Concept>()
            val children = ArrayList<Concept.Concept>()
            (snapshot.get("siblings") as? List<Map<String,Any>>)?.forEach{
                if (it.get("id") is String) {
                    siblings.add(Concept.Concept(it["id"] as String, it["name"] as? String))
                }
            }
            (snapshot.get("parents") as? List<Map<String,Any>>)?.forEach{
                if (it.get("id") is String) {
                    parents.add(Concept.Concept(it["id"] as String, it["name"] as? String))
                }
            }
            (snapshot.get("children") as? List<Map<String,Any>>)?.forEach{
                if (it.get("id") is String) {
                    children.add(Concept.Concept(it["id"] as String, it["name"] as? String))
                }
            }
            val concept = Concept(snapshot.id, snapshot.get("name") as? String, siblings, parents, children)
            onEvent(concept)
        }
        else {
            val test: Concept? = null
            onEvent(test);
        }
    }

    fun onEvent(concept: Concept?)
}