import React, { useEffect, useState, useMemo } from "react";
import "../style/TimeSlotManage.css";
import TimeSlotService from "../services/TimeSlotService";

const STORAGE_KEY = "landRules"; // { SMALL: number[], LARGE: number[] }

const toHHMMSS = (v) => {
  if (!v) return "";
  const [h, m] = v.split(":");
  return `${h.padStart(2, "0")}:${m.padStart(2, "0")}:00`;
};

const isValidStep = (timeStr, stepSec) => {
  const [h, m] = timeStr.split(":").map(Number);
  const totalSec = h * 3600 + m * 60;
  return totalSec % stepSec === 0;
};

const TimeSlotManagePage = () => {
  const [slots, setSlots] = useState([]);
  const [rules, setRules] = useState({ SMALL: [], LARGE: [] });
  const [loading, setLoading] = useState(true);
  const [savingRules, setSavingRules] = useState(false);
  const [error, setError] = useState("");
  const [selectedType, setSelectedType] = useState("LAND"); // LAND or VOL

  const [newForm, setNewForm] = useState({
    startTime: "",
    endTime: "",
    capacity: 10,
    enabled: true,
  });

  const [editSlotId, setEditSlotId] = useState(null);
  const [editForm, setEditForm] = useState({
    label: "",
    capacity: 0,
    enabled: true,
  });

  // ✅ 데이터 로드 함수
  const load = async (type) => {
    try {
      setLoading(true);
      setError("");
      const res = await TimeSlotService.fetchByType(type);
      setSlots(res.data || []);

      // localStorage 규칙 불러오기
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        try {
          const parsed = JSON.parse(saved);
          setRules({
            SMALL: Array.isArray(parsed.SMALL) ? parsed.SMALL : [],
            LARGE: Array.isArray(parsed.LARGE) ? parsed.LARGE : [],
          });
        } catch {
          setRules({ SMALL: [], LARGE: [] });
        }
      }
    } catch (e) {
      console.error(e);
      setError("시간대 목록을 불러오지 못했습니다.");
      setSlots([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(selectedType);
  }, [selectedType]);

  const allIds = useMemo(() => slots.map((s) => s.timeSlotId), [slots]);

  // 규칙 관련
  const toggleRule = (type, slotId, checked) => {
  setRules((prev) => {
    const updated = { ...prev };

    if (checked) {
      // 1. 현재 type에 slotId 추가
      updated[type] = [...(updated[type] || []), slotId];

      // 2. 반대 type에서 slotId 제거 (중복 방지)
      const otherType = type === "SMALL" ? "LARGE" : "SMALL";
      updated[otherType] = (updated[otherType] || []).filter((id) => id !== slotId);
    } else {
      // 체크 해제 → 해당 type 배열에서 제거
      updated[type] = (updated[type] || []).filter((id) => id !== slotId);
    }

    return updated;
  });
};

  const clearAll = (type) => setRules((p) => ({ ...p, [type]: [] }));

  const saveRules = () => {
    setSavingRules(true);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(rules));
    setTimeout(() => {
      setSavingRules(false);
      alert("규칙 저장 완료");
    }, 200);
  };

  // 추가
  const handleNewChange = (e) => {
    const { name, type, checked, value } = e.target;
    setNewForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const submitNew = async (e) => {
    e.preventDefault();
    if (!newForm.startTime || !newForm.endTime) {
      return alert("시작/종료 시간을 입력하세요.");
    }

    if (!isValidStep(newForm.startTime, 3600) || !isValidStep(newForm.endTime, 3600)) {
      return alert("시간은 1시간 단위로만 설정 가능합니다.");
    }

    try {
      const dto = {
        startTime: toHHMMSS(newForm.startTime),
        endTime: toHHMMSS(newForm.endTime),
        capacity: Number(newForm.capacity),
        enabled: !!newForm.enabled,
        timeType: selectedType,
      };
      await TimeSlotService.create(dto);
      alert("추가되었습니다.");
      setNewForm({ startTime: "", endTime: "", capacity: 10, enabled: true });
      load(selectedType);
    } catch {
      alert("추가 실패");
    }
  };

  // 수정
  const startEdit = (slot) => {
    setEditSlotId(slot.timeSlotId);
    setEditForm({
      startTime: slot.startTime.slice(0,5), // "09:00"
      endTime: slot.endTime.slice(0,5),
      capacity: slot.capacity,
      enabled: !!slot.enabled,
    });
  };

  const cancelEdit = () => {
    setEditSlotId(null);
    setEditForm({ label: "", capacity: 0, enabled: true });
  };

  const handleEditChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditForm((p) => ({ ...p, [name]: type === "checkbox" ? checked : value }));
  };

  const saveEdit = async (id) => {
    try {
      const original = slots.find((s) => s.timeSlotId === id);

      if (!isValidStep(editForm.startTime, 3600) || !isValidStep(editForm.endTime, 3600)) {
        return alert("시간은 1시간 단위로만 설정 가능합니다.");
      }

      const dto = {
        startTime: toHHMMSS(editForm.startTime),
        endTime: toHHMMSS(editForm.endTime),
        capacity: Number(editForm.capacity),
        enabled: !!editForm.enabled,
        timeType: selectedType,
      };

      await TimeSlotService.update(id, dto);
      alert("수정되었습니다.");
      cancelEdit();
      load(selectedType);
    } catch (e) {
      console.error(e);
      alert("수정 실패");
    }
  };

  // 삭제
  const remove = async (id) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await TimeSlotService.delete(id);
      alert("삭제되었습니다.");
      load(selectedType);
    } catch {
      alert("삭제 실패");
    }
  };

  if (loading) return <div>불러오는 중입니다…</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="tspage">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon timeslot"></div>
          <div className="form_title">시간대 운영 관리</div>
        </div>
      </div>

      {/* 탭 */}
      <div className="ts-tabs" style={{ marginBottom: "1rem" }}>
        <button
          className={`ts-btn ${selectedType === "LAND" ? "ts-btn primary" : ""}`}
          onClick={() => setSelectedType("LAND")}
        >
          놀이터
        </button>
        <button
          className={`ts-btn ${selectedType === "VOL" ? "ts-btn primary" : ""}`}
          onClick={() => setSelectedType("VOL")}
          style={{ marginLeft: "6px" }}
        >
          봉사
        </button>
      </div>

      {/* 새 시간대 추가 */}
      <div className="ts-card" style={{ marginBottom: "1rem" }}>
        <strong>새 시간대 추가</strong>
        <form onSubmit={submitNew} className="ts-form" style={{ marginTop: "0.5rem" }}>
          
          <div className="form-group">
            <label>시작</label>
            <input 
              type="time" 
              name="startTime" 
              value={newForm.startTime} 
              onChange={handleNewChange} 
              required 
              step="3600" 
            />
          </div>

          <div className="form-group">
            <label>종료</label>
            <input 
              type="time" 
              name="endTime" 
              value={newForm.endTime} 
              onChange={handleNewChange} 
              required 
              step="3600" 
            />
          </div>

          <div className="form-group">
            <label>정원</label>
            <input 
              type="number" 
              name="capacity" 
              value={newForm.capacity} 
              min={1} 
              onChange={handleNewChange} 
            />
          </div>

          <div className="form-group">
            <label>활성</label>
            <input 
              type="checkbox" 
              name="enabled" 
              checked={newForm.enabled} 
              onChange={handleNewChange} 
            />
          </div>

          <button type="submit" className="ts-btn primary">추가</button>
        </form>
      </div>

      {/* 규칙 버튼 */}
      {selectedType === "LAND" && (
        <div className="ts-actions">
          <button className="ts-btn" onClick={() => clearAll("SMALL")}>소형견 전체 해제</button>
          <button className="ts-btn" onClick={() => clearAll("LARGE")}>대형견 전체 해제</button>
          <button className="ts-btn ts-btn primary" onClick={saveRules} disabled={savingRules}>
            {savingRules ? "저장 중…" : "규칙 저장"}
          </button>
        </div>
      )}

      {/* 테이블 */}
      <div className="table type2 responsive border">
        <table className={`ts-table ${selectedType === "LAND" ? "land" : "vol"}`}>
          <thead>
            <tr>
              <th>ID</th>
              <th>라벨</th>
              <th>정원</th>
              <th>활성</th>
              {selectedType === "LAND" && <th>소형 허용</th>}
              {selectedType === "LAND" && <th>대형 허용</th>}
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {slots.map((s) => (
              <tr key={s.timeSlotId}>
                <td>{s.timeSlotId}</td>
                <td>
                  {editSlotId === s.timeSlotId ? (
                    <>
                      <input
                        type="time"
                        name="startTime"
                        value={editForm.startTime}
                        onChange={handleEditChange}
                        step="3600" 
                      />
                      ~
                      <input
                        type="time"
                        name="endTime"
                        value={editForm.endTime}
                        onChange={handleEditChange}
                        step="3600" 
                      />
                    </>
                  ) : (
                    s.label
                  )}
                </td>
                <td>
                  {editSlotId === s.timeSlotId ? (
                    <input
                      className="ts-edit-input"
                      type="number"
                      name="capacity"
                      value={editForm.capacity}
                      onChange={handleEditChange}
                      min={1}
                    />
                  ) : (
                    s.capacity ?? "-"
                  )}
                </td>
                <td>
                  {editSlotId === s.timeSlotId ? (
                    <input
                      type="checkbox"
                      name="enabled"
                      checked={editForm.enabled}
                      onChange={handleEditChange}
                    />
                  ) : (
                    s.enabled ? <span className="badge-yes">Y</span> : <span className="badge-no">N</span>
                  )}
                </td>
                
                {selectedType === "LAND" && (
                  <td>
                    <input
                      type="checkbox"
                      checked={(rules.SMALL || []).includes(s.timeSlotId)}
                      disabled={editSlotId === s.timeSlotId} 
                      onChange={(e) => toggleRule("SMALL", s.timeSlotId, e.target.checked)}
                    />
                  </td>
                )}

                {selectedType === "LAND" && (
                  <td>
                    <input
                      type="checkbox"
                      checked={(rules.LARGE || []).includes(s.timeSlotId)}
                      disabled={editSlotId === s.timeSlotId} 
                      onChange={(e) => toggleRule("LARGE", s.timeSlotId, e.target.checked)}
                    />
                  </td>
                )}

                <td>
                  {editSlotId === s.timeSlotId ? (
                    <><div className="button-group">
                      <button className="ts-btn ts-btn primary" onClick={() => saveEdit(s.timeSlotId)}>저장</button>
                      <button className="ts-btn" onClick={cancelEdit}>취소</button>
                    </div>
                    </>
                  ) : (
                    <><div className="button-group">
                       <button 
                      className="ts-btn" 
                      style={{ opacity: s.hasFutureReserve ? 0.5 : 1 }} // 회색 느낌만 주기
                      onClick={() => {
                        if (s.hasFutureReserve) {
                          alert("이 시간대에는 이미 예약이 있어 수정할 수 없습니다.");
                        } else {
                          startEdit(s);
                        }
                      }}
                    >
                      수정
                    </button>

                    <button 
                      className="ts-btn danger" 
                      style={{ opacity: s.hasFutureReserve ? 0.5 : 1 }}
                      onClick={() => {
                        if (s.hasFutureReserve) {
                          alert("이 시간대에는 이미 예약이 있어 삭제할 수 없습니다.");
                        } else {
                          remove(s.timeSlotId);
                        }
                      }}
                    >
                      삭제
                    </button>
                    </div>
                    </>
                  )}
                </td>
              </tr>
            ))}

            {slots.length === 0 && (
              <tr>
                <td colSpan={selectedType === "LAND" ? 7 : 5} style={{ textAlign: "center", padding: "12px" }}>
                  표시할 시간대가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TimeSlotManagePage;