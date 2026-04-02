"use client";

import { useEffect, useState } from "react";
import { statisticsApi, DashboardStats } from "@/api/statistics";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const router = useRouter();

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await statisticsApi.getDashboardStats();
        // @ts-ignore
        if (res.code === 200) {
          // @ts-ignore
          setStats(res.data);
        } else {
          // @ts-ignore
          setError(res.msg || "Failed to load dashboard data");
        }
      } catch (err: any) {
        if (err?.response?.status === 403) {
          setError("无权限查看大盘数据 (仅 ADMIN 可见)");
        } else {
          setError("获取大盘数据失败");
        }
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading dashboard...</div>;
  }

  if (error) {
    return <div className="p-8 text-center text-red-500 font-semibold">{error}</div>;
  }

  if (!stats) return null;

  return (
    <div className="container mx-auto p-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">数据统计大盘</h1>
        <button 
          onClick={() => router.push('/events')}
          className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-md transition-colors"
        >
          返回活动列表
        </button>
      </div>

      {/* 核心指标卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center justify-center">
          <p className="text-sm text-gray-500 font-medium mb-2">总活动数</p>
          <p className="text-4xl font-bold text-blue-600">{stats.totalEvents}</p>
        </div>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center justify-center">
          <p className="text-sm text-gray-500 font-medium mb-2">总报名数</p>
          <p className="text-4xl font-bold text-green-600">{stats.totalEnrollments}</p>
        </div>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center justify-center">
          <p className="text-sm text-gray-500 font-medium mb-2">总签到数</p>
          <p className="text-4xl font-bold text-purple-600">{stats.totalCheckins}</p>
        </div>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center justify-center">
          <p className="text-sm text-gray-500 font-medium mb-2">短链总访问量</p>
          <p className="text-4xl font-bold text-orange-600">{stats.totalShortLinkVisits}</p>
        </div>
      </div>

      {/* 近期活动明细 */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 bg-gray-50">
          <h2 className="text-lg font-semibold text-gray-800">近期活动明细 (Top 5)</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-white border-b border-gray-100 text-sm text-gray-500">
                <th className="px-6 py-4 font-medium">活动名称</th>
                <th className="px-6 py-4 font-medium">访问量 (短链)</th>
                <th className="px-6 py-4 font-medium">报名人数</th>
                <th className="px-6 py-4 font-medium">签到人数</th>
                <th className="px-6 py-4 font-medium">签到率</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {stats.recentEventStats.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                    暂无活动数据
                  </td>
                </tr>
              ) : (
                stats.recentEventStats.map((eventStat) => {
                  const checkinRate = eventStat.enrollmentCount > 0 
                    ? Math.round((eventStat.checkinCount / eventStat.enrollmentCount) * 100) 
                    : 0;
                  
                  return (
                    <tr key={eventStat.eventId} className="hover:bg-gray-50 transition-colors">
                      <td className="px-6 py-4 font-medium text-gray-800">{eventStat.eventTitle}</td>
                      <td className="px-6 py-4 text-orange-600">{eventStat.visitCount}</td>
                      <td className="px-6 py-4 text-green-600">{eventStat.enrollmentCount}</td>
                      <td className="px-6 py-4 text-purple-600">{eventStat.checkinCount}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <div className="w-full bg-gray-200 rounded-full h-2 max-w-[100px]">
                            <div 
                              className="bg-purple-500 h-2 rounded-full" 
                              style={{ width: `${checkinRate}%` }}
                            ></div>
                          </div>
                          <span className="text-sm text-gray-600">{checkinRate}%</span>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
