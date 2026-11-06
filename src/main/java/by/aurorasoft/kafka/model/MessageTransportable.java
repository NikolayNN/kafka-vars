package by.aurorasoft.kafka.model;

import by.nhorushko.aurutils.message.MessageType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.Nullable;

import java.util.Collections;
import java.util.Map;

@Value
@FieldNameConstants
public class MessageTransportable {

    @Nullable
    Long id;
    /**
     * seconds
     */
    long time;
    float latitude;
    float longitude;
    int altitude;
    int speed;
    int amountSatellite;
    int course;
    /**
     * kilometers
     */
    double gpsOdometer;
    /**
     * GSM level strength (Percent)
     */
    int gsmStrength;
    /**
     * tracker battery charge (Percent)
     */
    int battery;
    /**
     * tracker battery voltage (mV)
     */
    int batteryVoltage;

    /**
     * external power supply of the device (Volt)
     */
    float onboardVoltage;

    /**
     * acceleration of acceleration (G)
     */
    float ecoAcceleration;
    /**
     * acceleration of braking (G)
     */
    float ecoBraking;

    /**
     * cornering acceleration (G)
     */
    float ecoCornering;

    /**
     * bump acceleration (G)
     */
    float ecoBump;

    String params;
    @AvroDefault("{}")
    Map<String, String> strParams;
    long unitId;
    boolean isArchive;
    MessageType type;

    public MessageTransportable(
            Long id,
            long time,
            float latitude,
            float longitude,
            int altitude,
            int speed,
            int amountSatellite,
            int course,
            double gpsOdometer,
            int gsmStrength,
            int battery,
            int batteryVoltage,
            float onboardVoltage,
            float ecoAcceleration,
            float ecoBraking,
            float ecoCornering,
            float ecoBump,
            String params,
            long unitId,
            boolean isArchive,
            MessageType type
    ) {
        this(id, time, latitude, longitude, altitude, speed, amountSatellite, course,
                gpsOdometer, gsmStrength, battery, batteryVoltage, onboardVoltage,
                ecoAcceleration, ecoBraking, ecoCornering, ecoBump, params,
                Collections.emptyMap(), unitId, isArchive, type);
    }

    @JsonCreator
    public MessageTransportable(
            @JsonProperty("id") Long id,
            @JsonProperty("time") long time,
            @JsonProperty("latitude") float latitude,
            @JsonProperty("longitude") float longitude,
            @JsonProperty("altitude") int altitude,
            @JsonProperty("speed") int speed,
            @JsonProperty("amountSatellite") int amountSatellite,
            @JsonProperty("course") int course,
            @JsonProperty("gpsOdometer") double gpsOdometer,
            @JsonProperty("gsmStrength") int gsmStrength,
            @JsonProperty("battery") int battery,
            @JsonProperty("batteryVoltage") int batteryVoltage,
            @JsonProperty("onboardVoltage") float onboardVoltage,
            @JsonProperty("ecoAcceleration") float ecoAcceleration,
            @JsonProperty("ecoBraking") float ecoBraking,
            @JsonProperty("ecoCornering") float ecoCornering,
            @JsonProperty("ecoBump") float ecoBump,
            @JsonProperty("params") String params,
            @JsonProperty("strParams") Map<String, String> strParams,
            @JsonProperty("unitId") long unitId,
            @JsonProperty("isArchive") boolean isArchive,
            @JsonProperty("type") MessageType type
    ) {
        this.id = id;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.amountSatellite = amountSatellite;
        this.course = course;
        this.gpsOdometer = gpsOdometer;
        this.gsmStrength = gsmStrength;
        this.battery = battery;
        this.batteryVoltage = batteryVoltage;
        this.onboardVoltage = onboardVoltage;
        this.ecoAcceleration = ecoAcceleration;
        this.ecoBraking = ecoBraking;
        this.ecoCornering = ecoCornering;
        this.ecoBump = ecoBump;
        this.params = params;
        this.strParams = strParams == null ? Collections.emptyMap() : strParams;
        this.unitId = unitId;
        this.isArchive = isArchive;
        this.type = type;
    }
}
