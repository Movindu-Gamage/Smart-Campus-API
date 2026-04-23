package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
public class SensorRoom {

    // GET /api/v1/rooms — returns all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = DataStore.ROOMS;
        return Response.ok(rooms).build();
    }

    // GET /api/v1/rooms/{roomId} — returns one specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        for (Room room : DataStore.ROOMS) {
            if (room.getId().equals(roomId)) {
                return Response.ok(room).build();
            }
        }
        // If we get here, no room was found with that ID
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Room not found\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // POST /api/v1/rooms — creates a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        // Check if a room with this ID already exists
        for (Room existing : DataStore.ROOMS) {
            if (existing.getId().equals(room.getId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Room with this ID already exists\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
        DataStore.ROOMS.add(room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // DELETE /api/v1/rooms/{roomId} — deletes a room
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room roomToDelete = null;

        // Find the room
        for (Room room : DataStore.ROOMS) {
            if (room.getId().equals(roomId)) {
                roomToDelete = room;
                break;
            }
        }

        // Room doesn't exist
        if (roomToDelete == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Block deletion if room still has sensors
        if (!roomToDelete.getSensorIds().isEmpty()) {
            throw new com.smartcampus.exception.RoomNotEmptyException(
                    "Cannot delete room '" + roomId + "' — it still has active sensors assigned to it."
            );
        }

        DataStore.ROOMS.remove(roomToDelete);
        return Response.ok("{\"message\": \"Room deleted successfully\"}").build();
    }
}