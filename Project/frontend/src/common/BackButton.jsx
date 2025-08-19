// src/components/common/BackButton.jsx
import { useNavigate } from "react-router-dom";

function BackButton({ label = "이전", className = "btn" }) {
  const navigate = useNavigate();

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <button type="button" className={className} onClick={handleBack}>
      <span>{label}</span>
    </button>
  );
}

export default BackButton;
