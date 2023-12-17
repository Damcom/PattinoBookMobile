package com.company.damprovafinal.mdui.booksdetails

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.company.damprovafinal.databinding.FragmentBooksdetailsDetailBinding
import com.company.damprovafinal.mdui.EntityKeyUtil
import com.company.damprovafinal.mdui.InterfacedFragment
import com.company.damprovafinal.mdui.UIConstants
import com.company.damprovafinal.repository.OperationResult
import com.company.damprovafinal.R
import com.company.damprovafinal.viewmodel.booksdetails.BooksDetailsViewModel
import com.sap.cloud.android.odata.entitycontainer.EntityContainerMetadata.EntitySets
import com.sap.cloud.android.odata.entitycontainer.BooksDetails
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader


/**
 * A fragment representing a single BooksDetails detail screen.
 * This fragment is contained in an BooksDetailsActivity.
 */
class BooksDetailsDetailFragment : InterfacedFragment<BooksDetails, FragmentBooksdetailsDetailBinding>() {

    /** BooksDetails entity to be displayed */
    private lateinit var booksDetailsEntity: BooksDetails

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private var objectHeader: ObjectHeader? = null

    /** View model of the entity type that the displayed entity belongs to */
    private lateinit var viewModel: BooksDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menu = R.menu.itemlist_view_options
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentBinding.handler = this
        return fragmentBinding.root
    }

    override  fun  initBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentBooksdetailsDetailBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            currentActivity = it
            viewModel = ViewModelProvider(it)[BooksDetailsViewModel::class.java]
            viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
                onDeleteComplete(result)
            }

            viewModel.selectedEntity.observe(viewLifecycleOwner) { entity ->
                booksDetailsEntity = entity
                fragmentBinding.booksDetails = entity
                setupObjectHeader()
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.update_item -> {
                listener?.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, booksDetailsEntity)
                true
            }
            R.id.delete_item -> {
                listener?.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null)
                true
            }
            else -> super.onMenuItemSelected(menuItem)
        }
    }

    /**
     * Completion callback for delete operation
     *
     * @param [result] of the operation
     */
    private fun onDeleteComplete(result: OperationResult<BooksDetails>) {
        progressBar?.let {
            it.visibility = View.INVISIBLE
        }
        viewModel.removeAllSelected()
        result.error?.let {
            showError(getString(R.string.delete_failed_detail))
            return
        }
        listener?.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, booksDetailsEntity)
    }


    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private fun setDetailImage(objectHeader: ObjectHeader, booksDetailsEntity: BooksDetails) {
        if (booksDetailsEntity.getOptionalValue(BooksDetails.dettaglio) != null && booksDetailsEntity.getOptionalValue(BooksDetails.dettaglio).toString().isNotEmpty()) {
            objectHeader.detailImageCharacter = booksDetailsEntity.getOptionalValue(BooksDetails.dettaglio).toString().substring(0, 1)
        } else {
            objectHeader.detailImageCharacter = "?"
        }
    }

    /**
     * Setup ObjectHeader with an instance of booksDetailsEntity
     */
    private fun setupObjectHeader() {
        val secondToolbar = currentActivity.findViewById<Toolbar>(R.id.secondaryToolbar)
        if (secondToolbar != null) {
            secondToolbar.title = booksDetailsEntity.entityType.localName
        } else {
            currentActivity.title = booksDetailsEntity.entityType.localName
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader)
        val dataValue = booksDetailsEntity.getOptionalValue(BooksDetails.dettaglio)

        objectHeader?.let {
            it.apply {
                headline = dataValue?.toString()
                subheadline = EntityKeyUtil.getOptionalEntityKey(booksDetailsEntity)
                body = "You can set the header body text here."
                footnote = "You can set the header footnote here."
                description = "You can add a detailed item description here."
            }
            it.setTag("#tag1", 0)
            it.setTag("#tag3", 2)
            it.setTag("#tag2", 1)

            setDetailImage(it, booksDetailsEntity)
            it.visibility = View.VISIBLE
        }
    }
}
