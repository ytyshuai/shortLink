"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { eventApi, Event } from "@/api/event";
import { enrollmentApi } from "@/api/enrollment";
import { Button } from "@/components/ui/button";

export default function CheckinPage() {
  const params = useParams();
  const router = useRouter();
  const eventId = Number(params.eventId);
  
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [checkingIn, setCheckingIn] = useState(false);

  useEffect(() => {
    if (!eventId) return;
    
    const fetchEvent = async () => {
      try {
        const res = await eventApi.getEventById(eventId);
        // @ts-ignore
        if (res.code === 200) {
          // @ts-ignore
          setEvent(res.data);
        } else {
          // @ts-ignore
          alert(res.msg);
        }
      } catch (error) {
        console.error("Failed to fetch event", error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchEvent();
  }, [eventId]);

  const handleCheckin = async () => {
    setCheckingIn(true);
    try {
      const res = await enrollmentApi.checkIn(eventId);
      // @ts-ignore
      if (res.code === 200) {
        alert("签到成功！");
        router.push("/events");
      } else {
        // @ts-ignore
        alert(res.msg);
      }
    } catch (error) {
      console.error("签到失败", error);
      alert("签到失败，请稍后再试或联系管理员");
    } finally {
      setCheckingIn(false);
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
  }

  if (!event) {
    return <div className="min-h-screen flex items-center justify-center text-red-500">活动不存在或已被删除</div>;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl overflow-hidden">
        <div className="bg-primary p-6 text-center text-white">
          <h1 className="text-2xl font-bold mb-2">活动签到</h1>
          <p className="opacity-90">请确认您的签到信息</p>
        </div>
        
        <div className="p-6 space-y-6">
          <div className="space-y-4">
            <div>
              <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">活动名称</h2>
              <p className="mt-1 text-lg font-medium text-gray-900">{event.title}</p>
            </div>
            
            <div>
              <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">活动时间</h2>
              <p className="mt-1 text-gray-900">
                {new Date(event.startTime).toLocaleString()} <br/>
                至 {new Date(event.endTime).toLocaleString()}
              </p>
            </div>
            
            <div>
              <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">活动地点</h2>
              <p className="mt-1 text-gray-900">{event.location}</p>
            </div>
          </div>
          
          <div className="pt-6 border-t border-gray-200">
            <Button 
              className="w-full py-6 text-lg rounded-xl"
              onClick={handleCheckin}
              disabled={checkingIn}
            >
              {checkingIn ? "正在签到..." : "确认签到"}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
