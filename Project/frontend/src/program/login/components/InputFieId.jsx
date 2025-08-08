import React from 'react';

//공용 인풋 필드 컴포넌트
//param : {String} label - 라벨 텍스트
//param : {String} name - input name
//param : {String} value - 현재 입력값
//param : {function} onChange - 입력값 변경 핸들러
//param : {String} type - input type

const InputFieId = ({label, name, value, onChange, type = 'text'}) => {
    return(
        <div className='input_box'>
            <label>{label}</label>
            <input type={type} name={name} value={value} onChange={onChange} />
        </div>
    )
}

export default InputFieId;