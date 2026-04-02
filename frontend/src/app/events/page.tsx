"use client";

import { useEffect, useState } from "react";
import { eventApi, Event } from "@/api/event";
import { shortLinkApi } from "@/api/shortLink";
import { enrollmentApi } from "@/api/enrollment";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

import { QRCodeSVG } from "qrcode.react";

export default function EventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [shortLink, setShortLink] = useState<{ [key: number]: string }>({});
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

  const handleShare = async (event: Event) => {
    try {
      if (shortLink[event.id]) {
        return; // Already generated
      }
      const targetUrl = `${window.location.origin}/events/${event.id}`;
      const res = await shortLinkApi.createShortLink({
        originalUrl: targetUrl,
        eventId: event.id
      });
      // @ts-ignore
      if (res.code === 200) {
        // @ts-ignore
        const sLink = `http://localhost:8080/api/short-link/${res.data.shortCode}`;
        setShortLink(prev => ({ ...prev, [event.id]: sLink }));
      }
    } catch (error) {
      console.error("Failed to generate short link", error);
      alert("Failed to generate short link");
    }
  };

  const handleEnroll = async (eventId: number) => {
    try {
      const res = await enrollmentApi.enroll(eventId);
      // @ts-ignore
      if (res.code === 200) {
        alert("Enrollment successful!");
      } else {
        // @ts-ignore
        alert(res.msg);
      }
    } catch (error) {
      console.error("Failed to enroll", error);
      alert("Failed to enroll");
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
              
              {shortLink[event.id] && (
                <div className="mb-4 p-4 bg-gray-50 rounded-md flex flex-col items-center gap-2">
                  <p className="text-sm font-medium text-blue-600 break-all text-center">
                    <a href={shortLink[event.id]} target="_blank" rel="noreferrer">{shortLink[event.id]}</a>
                  </p>
                  <QRCodeSVG value={shortLink[event.id]} size={100} />
                </div>
              )}

              <div className="flex justify-end gap-2 mt-auto">
                <Button onClick={() => handleEnroll(event.id)}>Enroll</Button>
                <Button variant="outline" onClick={() => handleShare(event)}>Share</Button>
                <Button variant="destructive" onClick={() => handleDelete(event.id)}>Delete</Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
