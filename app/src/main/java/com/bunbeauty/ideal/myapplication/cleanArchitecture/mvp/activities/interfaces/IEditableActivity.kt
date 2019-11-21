package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.EditableEntity

interface IEditableActivity {
    fun goToEditing(editableEntity: EditableEntity)
}