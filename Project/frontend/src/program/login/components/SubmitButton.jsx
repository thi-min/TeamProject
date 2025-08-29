//공용 버튼 컴포넌트

import React from 'react';

//제출용 버튼 컴포넌트
const SubmitButton = ({text}) => {
    return(
        <button className='submit_btn' type='submit'>
            {text}
        </button>
    )
}
export default SubmitButton;