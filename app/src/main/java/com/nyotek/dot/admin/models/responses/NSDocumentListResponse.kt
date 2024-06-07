package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSDocumentListResponse(

	@field:SerializedName("data")
	val data: MutableList<DocumentDataItem> = arrayListOf()
)

data class DocumentDataItem(

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("document_number")
	val documentNumber: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("file_id")
	val fileId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("document_url")
	val documentUrl: String? = null,

	@field:SerializedName("document_type")
	val documentType: String? = null,

	@field:SerializedName("item_position")
	var itemPosition: Int = 0
)
