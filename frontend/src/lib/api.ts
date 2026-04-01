import request from '@/lib/request';

export const login = (data: any) => {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data
    });
};