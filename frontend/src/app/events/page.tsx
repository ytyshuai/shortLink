"use client";

import { useEffect, useState } from "react";
import { eventApi, Event } from "@/api/event";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

export default function EventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  const fetchEvents = async () => {
    try {
      const res = await eventApi.getEvents();
      // @ts-ignore
      if (res.code === 200) {
        // @ts-ignore
        setEvents(res.data);
      }
    } catch (error) {
      console.error("Failed to fetch events", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents();
  }, []);

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this event?")) return;
    try {
      const res = await eventApi.deleteEvent(id);
      // @ts-ignore
      if (res.code === 200) {
        fetchEvents();
      } else {
        // @ts-ignore
        alert(res.msg);
      }
    } catch (error) {
      console.error("Failed to delete event", error);
      alert("Failed to delete event");
    }
  };

  return (
    <div className="container mx-auto p-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Events</h1>
        <Button onClick={() => router.push("/events/create")}>Create Event</Button>
      </div>

      {loading ? (
        <p>Loading...</p>
      ) : events.length === 0 ? (
        <p className="text-gray-500">No events found.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {events.map((event) => (
            <div key={event.id} className="border rounded-lg p-6 shadow-sm flex flex-col">
              <h2 className="text-xl font-semibold mb-2">{event.title}</h2>
              <p className="text-gray-600 mb-4 flex-grow">{event.description}</p>
              <div className="text-sm text-gray-500 space-y-1 mb-4">
                <p>📍 {event.location}</p>
                <p>🕒 {new Date(event.startTime).toLocaleString()} - {new Date(event.endTime).toLocaleString()}</p>
                <p>👥 Capacity: {event.capacity === 0 ? "Unlimited" : event.capacity}</p>
                <p>🏷️ Status: {event.status}</p>
              </div>
              <div className="flex justify-end gap-2 mt-auto">
                <Button variant="destructive" onClick={() => handleDelete(event.id)}>Delete</Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
