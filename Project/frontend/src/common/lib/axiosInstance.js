import axios from 'axios';

//axios 인스턴스 생성
const axiosInstance = axios.create({
    baseURL: 'http://localghost:8090',//백엔드 주소
    headers:{
        'Content-Type': 'application/json',
    },
})