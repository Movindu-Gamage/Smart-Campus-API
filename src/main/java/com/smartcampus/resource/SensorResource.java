package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
public class SensorResource {

    // GET /api/v1/sensors — returns all sensors, with optional ?type= filter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>();

        for (Sensor sensor : DataStore.SENSORS) {
            if (type == null || sensor.getType().equalsIgnoreCase(type)) {
                result.add(sensor);
            }
        }
        return Response.ok(result).build();
    }

    // GET /api/v1/sensors/{sensorId} — returns one specific sensor
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        for (Sensor sensor : DataStore.SENSORS) {
            if (sensor.getId().equals(sensorId)) {
                return Response.ok(sensor).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Sensor not found\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // POST /api/v1/sensors — registers a new sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        // Validate that the roomId actually exists
        boolean roomExists = false;
        for (com.smartcampus.model.Room room : DataStore.ROOMS) {
            if (room.getId().equals(sensor.getRoomId())) {
                roomExists = true;
                break;
            }
        }

        if (!roomExists) {
            throw new com.smartcampus.exception.LinkedResourceNotFoundException(
                    "Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }

        // Check sensor ID doesn't already exist
        for (Sensor existing : DataStore.SENSORS) {
            if (existing.getId().equals(sensor.getId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Sensor with this ID already exists\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }

        // Add sensor to the list
        DataStore.SENSORS.add(sensor);

        // Also add the sensor ID to the room's sensorIds list
        for (com.smartcampus.model.Room room : DataStore.ROOMS) {
            if (room.getId().equals(sensor.getRoomId())) {
                room.getSensorIds().add(sensor.getId());
                break;
            }
        }

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    // Sub-resource locator — delegates /sensors/{sensorId}/readings to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}