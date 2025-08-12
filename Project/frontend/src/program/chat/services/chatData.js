// 샘플 대화 데이터(초기값)
const sampleConversations = [
  {
    id: 1,
    email: "test@naver.com",
    name: "이순신",
    lastMessage: "감사합니다.",
    lastDate: "2025-07-14",
    new: false,
    messages: [
      { id: "m1", from: "other", text: "치와와 입양하고 싶습니다", time: "11:14" },
      { id: "m2", from: "me", text: "해당견의 상태.. etc", time: "13:05" },
      { id: "m3", from: "other", text: "감사합니다.", time: "13:12" },
    ],
  },
  {
    id: 2,
    email: "test2@hanmail.com",
    name: "이 황",
    lastMessage: "어떠어떠한 일로 어떤 상태이며 어떤 문제가 있는지 어떠하게 느끼시고 어떤 말로 해주십시오...",
    lastDate: "2025-07-14",
    new: true,
    messages: [
      { id: "m1", from: "other", text: "어떠어떠한 일로 어떤 상태이며...", time: "09:00" },
    ],
  },
];

export default sampleConversations;