package com.company.damprovafinal.viewmodel.books

import android.app.Application
import android.os.Parcelable

import com.company.damprovafinal.viewmodel.EntityViewModel
import com.sap.cloud.android.odata.entitycontainer.Books
import com.sap.cloud.android.odata.entitycontainer.EntityContainerMetadata.EntitySets

/*
 * Represents View model for Books
 *
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and return the view model of that
 * type. This is because the ViewModelStore of ViewModelProvider cannot not be able to tell the difference between
 * EntityViewModel<type1> and EntityViewModel<type2>.
 */
class BooksViewModel(application: Application): EntityViewModel<Books>(application, EntitySets.books, Books.title) {
    /**
     * Constructor for a specific view model with navigation data.
     * @param [navigationPropertyName] - name of the navigation property
     * @param [entityData] - parent entity (starting point of the navigation)
     */
    constructor(application: Application, navigationPropertyName: String, entityData: Parcelable): this(application) {
        EntityViewModel<Books>(application, EntitySets.books, Books.title, navigationPropertyName, entityData)
    }
}
