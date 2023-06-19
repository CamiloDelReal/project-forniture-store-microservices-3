package org.xapps.service.fornitureservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = Forniture.INDEX_NAME)
data class Forniture(
    @JsonProperty(value = ID_PROPERTY)
    @Field(type = FieldType.Keyword)
    var id: String? = null,

    @JsonProperty(value = NAME_PROPERTY)
    @Field(type = FieldType.Keyword)
    var name: String,

    @JsonProperty(value = DESCRIPTION_PROPERTY)
    @Field(type = FieldType.Keyword)
    var description: String,

    @JsonProperty(value = PRICE_PROPERTY)
    @Field(type = FieldType.Float)
    var price: Float,

    @JsonProperty(value = SMALL_PICTURE_PATH_PROPERTY)
    @Field(type = FieldType.Text)
    var smallPicturePath: String? = null,

    @JsonProperty(value = LARGE_PICTURE_PATH_PROPERTY)
    @Field(type = FieldType.Text)
    var largePicturePath: String? = null
) {

    companion object {
        const val INDEX_NAME = "fornitures"

        const val ID_PROPERTY = "id"
        const val NAME_PROPERTY = "name"
        const val DESCRIPTION_PROPERTY = "description"
        const val PRICE_PROPERTY = "price"
        const val SMALL_PICTURE_PATH_PROPERTY = "smallPicturePath"
        const val LARGE_PICTURE_PATH_PROPERTY = "largePicturePath"
    }

}