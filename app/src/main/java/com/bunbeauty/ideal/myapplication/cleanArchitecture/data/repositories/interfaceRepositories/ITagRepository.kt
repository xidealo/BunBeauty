package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag


interface ITagRepository {
    fun insert(tag: Tag)
    fun delete(tag: Tag)
    fun update(tag: Tag)
    fun get(): List<Tag>
}