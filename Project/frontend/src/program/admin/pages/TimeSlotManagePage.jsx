import React, { useEffect, useState, useMemo } from "react";
import {
  fetchLandTimeSlots,
  createTimeSlot,
  updateTimeSlot,
  deleteTimeSlot,
} from "../services/TimeSlotService";

const STORAGE_KEY = "landTypeRules"; // { SMALL: number[], LARGE: number[] }

// "HH:mm" -> "HH:mm:ss" 로 맞춰줌
const toHHMMSS = (v) => {
  if (!v) return "";
  const [h, m] = v.split(":");
  return `${h.padStart(2, "0")}:${m.padStart(2, "0")}:00`;
};

const TimeSlotManagePage = () => {
  const [slots, setSlots] = useState([]);
  const [rules, setRules] = useState({ SMALL: [], LARGE: [] });
  const [loading, setLoading] = useState(true);
  const [savingRules, setSavingRules] = useState(false);
  const [error, setError] = useState("");

  // 새 시간대 추가용
  const [newForm, setNewForm] = useState({
    startTime: "",
    endTime: "",
    capacity: 10,
    enabled: true,
    timeType: "LAND", // 반드시 LAND로 생성
  });

  // 수정 모드
  const [editSlotId, setEditSlotId] = useState(null);
  const [editForm, setEditForm] = useState({
    label: "",
    capacity: 0,
    enabled: true,
  });

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      setLoading(true);
      setError("");
      const res = await fetchLandTimeSlots();
      setSlots(res.data || []);

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
      setError("시간대 목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const allIds = useMemo(() => slots.map((s) => s.id), [slots]);

  // 규칙 토글
  const toggleRule = (type, slotId, checked) => {
    setRules((prev) => {
      const set = new Set(prev[type] || []);
      checked ? set.add(slotId) : set.delete(slotId);
      return { ...prev, [type]: Array.from(set) };
    });
  };

  const selectAll = (type) => setRules((p) => ({ ...p, [type]: allIds }));
  const clearAll = (type) => setRules((p) => ({ ...p, [type]: [] }));

  const saveRules = () => {
    setSavingRules(true);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(rules));
    setTimeout(() => {
      setSavingRules(false);
      alert("규칙 저장 완료");
    }, 200);
  };

  // ===== 추가(Create) =====
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

    try {
      const dto = {
        startTime: toHHMMSS(newForm.startTime),
        endTime: toHHMMSS(newForm.endTime),
        capacity: Number(newForm.capacity),
        enabled: !!newForm.enabled,
        timeType: "LAND",
      };
      await createTimeSlot(dto);
      alert("추가되었습니다.");
      setNewForm({ startTime: "", endTime: "", capacity: 10, enabled: true, timeType: "LAND" });
      load();
    } catch (err) {
      alert("추가 실패");
    }
  };

  // ===== 수정(Update) =====
  const startEdit = (slot) => {
    setEditSlotId(slot.id);
    setEditForm({
      label: slot.label,
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
      const dto = {
        // label은 서버에서 start/end 기반으로 갱신되지만
        // 현재 구조에서는 label, capacity, enabled만 수정
        label: editForm.label, // 서버가 label을 무시/재생성해도 무방
        capacity: Number(editForm.capacity),
        enabled: !!editForm.enabled,
      };
      await updateTimeSlot(id, dto);
      alert("수정되었습니다.");
      cancelEdit();
      load();
    } catch (e) {
      alert("수정 실패");
    }
  };

  // ===== 삭제(Delete) =====
  const remove = async (id) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await deleteTimeSlot(id);
      alert("삭제되었습니다.");
      load();
    } catch (e) {
      alert("삭제 실패");
    }
  };

  if (loading) return <div>불러오는 중입니다…</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h2>시간대 운영 관리 (놀이터)</h2>

      {/* 새 시간대 추가 */}
      <form onSubmit={submitNew} style={{ margin: "1rem 0", padding: "0.75rem", border: "1px solid #ddd" }}>
        <strong>새 시간대 추가</strong>
        <div style={{ display: "flex", gap: "0.5rem", alignItems: "center", marginTop: "0.5rem" }}>
          <label>시작</label>
          <input type="time" name="startTime" value={newForm.startTime} onChange={handleNewChange} required />
          <label>종료</label>
          <input type="time" name="endTime" value={newForm.endTime} onChange={handleNewChange} required />
          <label>정원</label>
          <input type="number" name="capacity" value={newForm.capacity} min={1} onChange={handleNewChange} />
          <label>활성</label>
          <input type="checkbox" name="enabled" checked={newForm.enabled} onChange={handleNewChange} />
          <button type="submit">추가</button>
        </div>
      </form>

      <div style={{ marginBottom: "1rem" }}>
        <button onClick={() => selectAll("SMALL")}>소형견 전체 허용</button>{" "}
        <button onClick={() => clearAll("SMALL")}>소형견 전체 해제</button>{" "}
        <button onClick={() => selectAll("LARGE")}>대형견 전체 허용</button>{" "}
        <button onClick={() => clearAll("LARGE")}>대형견 전체 해제</button>{" "}
        <button onClick={saveRules} disabled={savingRules}>
          {savingRules ? "저장 중…" : "규칙 저장"}
        </button>
      </div>

      <table border="1" cellPadding="6" style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>라벨</th>
            <th>정원</th>
            <th>활성</th>
            <th>소형 허용</th>
            <th>대형 허용</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          {slots.map((s) => (
            <tr key={s.id}>
              <td>{s.id}</td>
              <td>
                {editSlotId === s.id ? (
                  <input
                    type="text"
                    name="label"
                    value={editForm.label}
                    onChange={handleEditChange}
                  />
                ) : (
                  s.label
                )}
              </td>
              <td>
                {editSlotId === s.id ? (
                  <input
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
                {editSlotId === s.id ? (
                  <input
                    type="checkbox"
                    name="enabled"
                    checked={editForm.enabled}
                    onChange={handleEditChange}
                  />
                ) : s.enabled ? "Y" : "N"}
              </td>
              <td>
                <input
                  type="checkbox"
                  checked={(rules.SMALL || []).includes(s.id)}
                  onChange={(e) => toggleRule("SMALL", s.id, e.target.checked)}
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  checked={(rules.LARGE || []).includes(s.id)}
                  onChange={(e) => toggleRule("LARGE", s.id, e.target.checked)}
                />
              </td>
              <td>
                {editSlotId === s.id ? (
                  <>
                    <button onClick={() => saveEdit(s.id)}>저장</button>{" "}
                    <button onClick={cancelEdit}>취소</button>
                  </>
                ) : (
                  <>
                    <button onClick={() => startEdit(s)}>수정</button>{" "}
                    <button onClick={() => remove(s.id)}>삭제</button>
                  </>
                )}
              </td>
            </tr>
          ))}
          {slots.length === 0 && (
            <tr>
              <td colSpan={7}>표시할 시간대가 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default TimeSlotManagePage;