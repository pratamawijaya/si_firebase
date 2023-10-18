package id.pratama.week10firebase.presentation.home.rvitem

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import id.pratama.week10firebase.R
import id.pratama.week10firebase.databinding.ItemNotesBinding
import id.pratama.week10firebase.model.Note

class NoteRvItem(private val note: Note) : BindableItem<ItemNotesBinding>() {
    override fun bind(viewBinding: ItemNotesBinding, position: Int) {
        viewBinding.tvTitle.text = note.judul
        viewBinding.tvDesc.text = note.deskripsi
        viewBinding.tvCreatedAt.text = note.createdAt.toString()
    }

    override fun getLayout(): Int {
        return R.layout.item_notes
    }

    override fun initializeViewBinding(view: View): ItemNotesBinding {
        return ItemNotesBinding.bind(view)
    }

}