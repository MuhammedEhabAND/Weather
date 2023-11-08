package inc.moe.weather.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather", primaryKeys = ["timeZone", "isCached"])

data class DatabaseWeather(
    val long:Double,
    val lat:Double,
    @PrimaryKey
    val timeZone:String,
    val weatherType :String,
    val temp:Double,
    val image:String,
    @PrimaryKey
    val isCached:Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(long)
        parcel.writeDouble(lat)
        parcel.writeString(timeZone)
        parcel.writeString(weatherType)
        parcel.writeDouble(temp)
        parcel.writeString(image)
        parcel.writeByte(if (isCached) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DatabaseWeather> {
        override fun createFromParcel(parcel: Parcel): DatabaseWeather {
            return DatabaseWeather(parcel)
        }

        override fun newArray(size: Int): Array<DatabaseWeather?> {
            return arrayOfNulls(size)
        }
    }
}