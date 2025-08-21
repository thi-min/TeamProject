import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import MemberDeleteButton from "./MemberDeleteButton";

import api from "../../../common/api/axios";

export default function Mypage() {
  const [myInfo, setMyInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        // Authorization 헤더는 api 인터셉터가 자동 부착
        const res = await api.get("/member/mypage/me");
        if (!mounted) return;
        setMyInfo(res.data); // { memberNum, memberId, memberName, memberState }
      } catch (e) {
        console.error(e);
        alert("내 정보 조회에 실패했습니다. 다시 로그인해주세요");
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  if (loading) {
    return <div className="member_page">로딩중...</div>;
  }

  return (
    <div className="my_page">
      <div className="title_box">
        <div className="title">마이 페이지</div>
      </div>
      <div className="member_area">
        <ul className="my_menu">
          <li className="link_item type1">
            <Link to="/member/update-password" state={{ mode: "self" }}>
              비밀번호 변경
            </Link>
          </li>
          <li className="link_item type2">
            <Link to="/member/mypage/memberdata">회원 정보</Link>
          </li>
          <li className="link_item type3">
            <Link to="/member/mypage/reserves">예약 내역 조회</Link>
          </li>
          <li className="link_item type6">
            <Link to="">예약 시간대 관리</Link>
          </li>
          <li className="link_item type5">
            <MemberDeleteButton
              memberNum={myInfo?.memberNum}
              className="form_flex"
            />
          </li>
        </ul>
      </div>
    </div>
  );
}
