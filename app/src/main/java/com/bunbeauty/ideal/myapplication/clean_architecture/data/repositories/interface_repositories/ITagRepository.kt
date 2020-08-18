package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.DeleteTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.UpdateTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag


interface ITagRepository {
    fun insert(tag: Tag, insertTagCallback: InsertTagCallback)
    fun delete(tag: Tag, deleteTagCallback: DeleteTagCallback)
    fun update(tag: Tag, updateTagCallback: UpdateTagCallback)
    fun get()
}