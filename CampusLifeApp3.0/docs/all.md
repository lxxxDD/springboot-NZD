# 校园生活服务平台 API 文档


**简介**:校园生活服务平台 API 文档


**HOST**:http://localhost:8080


**联系人**:开发团队


**Version**:1.0.0


**接口路径**:/v3/api-docs/all


[TOC]






# 用户模块


## 获取用户信息


**接口地址**:`/api/user/profile`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserInfoVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||UserInfoVO|UserInfoVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;studentId||string||
|&emsp;&emsp;username||string||
|&emsp;&emsp;email||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;avatar||string||
|&emsp;&emsp;balance||number||
|&emsp;&emsp;points||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"studentId": "",
		"username": "",
		"email": "",
		"phone": "",
		"avatar": "",
		"balance": 0,
		"points": 0,
		"createTime": ""
	},
	"message": ""
}
```


## 更新用户信息


**接口地址**:`/api/user/profile`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "avatar": "",
  "phone": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|updateProfileDTO|UpdateProfileDTO|body|true|UpdateProfileDTO|UpdateProfileDTO|
|&emsp;&emsp;username|||false|string||
|&emsp;&emsp;avatar|||false|string||
|&emsp;&emsp;phone|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 充值


**接口地址**:`/api/user/topup`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "amount": 0,
  "paymentMethod": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|topupDTO|TopupDTO|body|true|TopupDTO|TopupDTO|
|&emsp;&emsp;amount|||false|number||
|&emsp;&emsp;paymentMethod|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringBigDecimal|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 用户注册


**接口地址**:`/api/user/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "studentId": "",
  "username": "",
  "password": "",
  "email": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|registerDTO|RegisterDTO|body|true|RegisterDTO|RegisterDTO|
|&emsp;&emsp;studentId|||false|string||
|&emsp;&emsp;username|||false|string||
|&emsp;&emsp;password|||false|string||
|&emsp;&emsp;email|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultLoginVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||LoginVO|LoginVO|
|&emsp;&emsp;userInfo||UserInfoVO|UserInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;studentId||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;balance||number||
|&emsp;&emsp;&emsp;&emsp;points||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;token||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"userInfo": {
			"id": 0,
			"studentId": "",
			"username": "",
			"email": "",
			"phone": "",
			"avatar": "",
			"balance": 0,
			"points": 0,
			"createTime": ""
		},
		"token": ""
	},
	"message": ""
}
```


## 用户登录


**接口地址**:`/api/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "studentId": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginDTO|LoginDTO|body|true|LoginDTO|LoginDTO|
|&emsp;&emsp;studentId|||false|string||
|&emsp;&emsp;password|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultLoginVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||LoginVO|LoginVO|
|&emsp;&emsp;userInfo||UserInfoVO|UserInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;studentId||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;balance||number||
|&emsp;&emsp;&emsp;&emsp;points||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;token||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"userInfo": {
			"id": 0,
			"studentId": "",
			"username": "",
			"email": "",
			"phone": "",
			"avatar": "",
			"balance": 0,
			"points": 0,
			"createTime": ""
		},
		"token": ""
	},
	"message": ""
}
```


## 获取余额


**接口地址**:`/api/user/balance`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringBigDecimal|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


# 订单模块


## 更新订单状态


**接口地址**:`/api/orders/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取订单列表


**接口地址**:`/api/orders`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||
|type||query|false|string||
|status||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageVOOrderVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||PageVOOrderVO|PageVOOrderVO|
|&emsp;&emsp;list||array|OrderVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;buyerId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;buyerName||string||
|&emsp;&emsp;&emsp;&emsp;sellerId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;sellerName||string||
|&emsp;&emsp;&emsp;&emsp;orderType||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;paymentMethod||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;items||array|OrderItemVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;itemId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;itemName||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;itemImage||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;price||number||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;quantity||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;paidAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;completedAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;hasMore||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"list": [
			{
				"id": 0,
				"orderNo": "",
				"buyerId": 0,
				"buyerName": "",
				"sellerId": 0,
				"sellerName": "",
				"orderType": "",
				"totalAmount": 0,
				"paymentMethod": "",
				"status": "",
				"remark": "",
				"items": [
					{
						"id": 0,
						"itemId": 0,
						"itemName": "",
						"itemImage": "",
						"price": 0,
						"quantity": 0
					}
				],
				"paidAt": "",
				"completedAt": "",
				"createTime": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"hasMore": true
	},
	"message": ""
}
```


## 创建订单


**接口地址**:`/api/orders`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "itemId": 0,
  "type": "",
  "amount": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|createOrderDTO|CreateOrderDTO|body|true|CreateOrderDTO|CreateOrderDTO|
|&emsp;&emsp;itemId|||false|integer(int64)||
|&emsp;&emsp;type|||false|string||
|&emsp;&emsp;amount|||false|number||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOrderVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||OrderVO|OrderVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;orderNo||string||
|&emsp;&emsp;buyerId||integer(int64)||
|&emsp;&emsp;buyerName||string||
|&emsp;&emsp;sellerId||integer(int64)||
|&emsp;&emsp;sellerName||string||
|&emsp;&emsp;orderType||string||
|&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;paymentMethod||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;remark||string||
|&emsp;&emsp;items||array|OrderItemVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;itemId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;itemName||string||
|&emsp;&emsp;&emsp;&emsp;itemImage||string||
|&emsp;&emsp;&emsp;&emsp;price||number||
|&emsp;&emsp;&emsp;&emsp;quantity||integer(int32)||
|&emsp;&emsp;paidAt||string(date-time)||
|&emsp;&emsp;completedAt||string(date-time)||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"orderNo": "",
		"buyerId": 0,
		"buyerName": "",
		"sellerId": 0,
		"sellerName": "",
		"orderType": "",
		"totalAmount": 0,
		"paymentMethod": "",
		"status": "",
		"remark": "",
		"items": [
			{
				"id": 0,
				"itemId": 0,
				"itemName": "",
				"itemImage": "",
				"price": 0,
				"quantity": 0
			}
		],
		"paidAt": "",
		"completedAt": "",
		"createTime": ""
	},
	"message": ""
}
```


## 支付订单


**接口地址**:`/api/orders/{id}/pay`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "paymentMethod": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|payOrderDTO|PayOrderDTO|body|true|PayOrderDTO|PayOrderDTO|
|&emsp;&emsp;paymentMethod|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringBoolean|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 取消订单


**接口地址**:`/api/orders/{id}/cancel`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "reason": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|cancelOrderDTO|CancelOrderDTO|body|true|CancelOrderDTO|CancelOrderDTO|
|&emsp;&emsp;reason|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取订单详情


**接口地址**:`/api/orders/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOrderVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||OrderVO|OrderVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;orderNo||string||
|&emsp;&emsp;buyerId||integer(int64)||
|&emsp;&emsp;buyerName||string||
|&emsp;&emsp;sellerId||integer(int64)||
|&emsp;&emsp;sellerName||string||
|&emsp;&emsp;orderType||string||
|&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;paymentMethod||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;remark||string||
|&emsp;&emsp;items||array|OrderItemVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;itemId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;itemName||string||
|&emsp;&emsp;&emsp;&emsp;itemImage||string||
|&emsp;&emsp;&emsp;&emsp;price||number||
|&emsp;&emsp;&emsp;&emsp;quantity||integer(int32)||
|&emsp;&emsp;paidAt||string(date-time)||
|&emsp;&emsp;completedAt||string(date-time)||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"orderNo": "",
		"buyerId": 0,
		"buyerName": "",
		"sellerId": 0,
		"sellerName": "",
		"orderType": "",
		"totalAmount": 0,
		"paymentMethod": "",
		"status": "",
		"remark": "",
		"items": [
			{
				"id": 0,
				"itemId": 0,
				"itemName": "",
				"itemImage": "",
				"price": 0,
				"quantity": 0
			}
		],
		"paidAt": "",
		"completedAt": "",
		"createTime": ""
	},
	"message": ""
}
```


# 通知模块


## 标记已读


**接口地址**:`/api/notifications/{id}/read`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 全部已读


**接口地址**:`/api/notifications/read-all`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取通知列表


**接口地址**:`/api/notifications`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


# 消息模块


## 标记已读


**接口地址**:`/api/messages/read/{conversationId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|conversationId||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 发送消息


**接口地址**:`/api/messages/send`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "receiverId": 0,
  "content": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|sendMessageDTO|SendMessageDTO|body|true|SendMessageDTO|SendMessageDTO|
|&emsp;&emsp;receiverId|||false|integer(int64)||
|&emsp;&emsp;content|||false|string||
|&emsp;&emsp;type|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取会话列表


**接口地址**:`/api/messages/conversations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListConversationVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array|ConversationVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;username||string||
|&emsp;&emsp;avatar||string||
|&emsp;&emsp;lastMessage||string||
|&emsp;&emsp;lastMessageTime||string(date-time)||
|&emsp;&emsp;unreadCount||integer(int32)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [
		{
			"id": 0,
			"userId": 0,
			"username": "",
			"avatar": "",
			"lastMessage": "",
			"lastMessageTime": "",
			"unreadCount": 0
		}
	],
	"message": ""
}
```


## 获取聊天记录


**接口地址**:`/api/messages/conversations/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageVOMessageVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||PageVOMessageVO|PageVOMessageVO|
|&emsp;&emsp;list||array|MessageVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;senderId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;senderName||string||
|&emsp;&emsp;&emsp;&emsp;senderAvatar||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;messageType||string||
|&emsp;&emsp;&emsp;&emsp;isMine||boolean||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;hasMore||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"list": [
			{
				"id": 0,
				"senderId": 0,
				"senderName": "",
				"senderAvatar": "",
				"content": "",
				"messageType": "",
				"isMine": true,
				"createTime": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"hasMore": true
	},
	"message": ""
}
```


## 删除会话


**接口地址**:`/api/messages/conversations/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


# 商品模块


## 获取商品详情


**接口地址**:`/api/market/items/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMarketItemVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||MarketItemVO|MarketItemVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;sellerId||integer(int64)||
|&emsp;&emsp;sellerName||string||
|&emsp;&emsp;sellerAvatar||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;price||number||
|&emsp;&emsp;originalPrice||number||
|&emsp;&emsp;category||string||
|&emsp;&emsp;conditionLevel||string||
|&emsp;&emsp;images||array|string|
|&emsp;&emsp;coverImage||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;viewCount||integer(int32)||
|&emsp;&emsp;favoriteCount||integer(int32)||
|&emsp;&emsp;isFavorite||boolean||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"sellerId": 0,
		"sellerName": "",
		"sellerAvatar": "",
		"title": "",
		"description": "",
		"price": 0,
		"originalPrice": 0,
		"category": "",
		"conditionLevel": "",
		"images": [],
		"coverImage": "",
		"status": "",
		"viewCount": 0,
		"favoriteCount": 0,
		"isFavorite": true,
		"createTime": ""
	},
	"message": ""
}
```


## 更新商品


**接口地址**:`/api/market/items/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "price": 0,
  "originalPrice": 0,
  "category": "",
  "conditionLevel": "",
  "images": [],
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|updateMarketItemDTO|UpdateMarketItemDTO|body|true|UpdateMarketItemDTO|UpdateMarketItemDTO|
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;price|||false|number||
|&emsp;&emsp;originalPrice|||false|number||
|&emsp;&emsp;category|||false|string||
|&emsp;&emsp;conditionLevel|||false|string||
|&emsp;&emsp;images|||false|array|string|
|&emsp;&emsp;description|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 删除商品


**接口地址**:`/api/market/items/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 更新商品状态


**接口地址**:`/api/market/items/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "status": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|updateItemStatusDTO|UpdateItemStatusDTO|body|true|UpdateItemStatusDTO|UpdateItemStatusDTO|
|&emsp;&emsp;status|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取商品列表


**接口地址**:`/api/market/items`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||
|category||query|false|string||
|keyword||query|false|string||
|sort||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageVOMarketItemListVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||PageVOMarketItemListVO|PageVOMarketItemListVO|
|&emsp;&emsp;list||array|MarketItemListVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;sellerName||string||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;price||number||
|&emsp;&emsp;&emsp;&emsp;coverImage||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;hasMore||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"list": [
			{
				"id": 0,
				"sellerName": "",
				"title": "",
				"price": 0,
				"coverImage": "",
				"status": "",
				"createTime": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"hasMore": true
	},
	"message": ""
}
```


## 发布商品


**接口地址**:`/api/market/items`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "price": 0,
  "originalPrice": 0,
  "category": "",
  "conditionLevel": "",
  "images": [],
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|createMarketItemDTO|CreateMarketItemDTO|body|true|CreateMarketItemDTO|CreateMarketItemDTO|
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;price|||false|number||
|&emsp;&emsp;originalPrice|||false|number||
|&emsp;&emsp;category|||false|string||
|&emsp;&emsp;conditionLevel|||false|string||
|&emsp;&emsp;images|||false|array|string|
|&emsp;&emsp;description|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 收藏-取消收藏


**接口地址**:`/api/market/items/{id}/favorite`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringBoolean|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取我的商品


**接口地址**:`/api/market/my-items`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|status||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListMarketItemListVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array|MarketItemListVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;sellerName||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;price||number||
|&emsp;&emsp;coverImage||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [
		{
			"id": 0,
			"sellerName": "",
			"title": "",
			"price": 0,
			"coverImage": "",
			"status": "",
			"createTime": ""
		}
	],
	"message": ""
}
```


# 文件上传


## 批量上传图片


**接口地址**:`/api/upload/images`


**请求方式**:`POST`


**请求数据类型**:`multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|files||query|true|array|file|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringListString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 上传单张图片


**接口地址**:`/api/upload/image`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|file||query|true|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


# 报修模块


## 获取报修列表


**接口地址**:`/api/repairs`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|status||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListRepairVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array|RepairVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;repairNo||string||
|&emsp;&emsp;location||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;images||array|string|
|&emsp;&emsp;status||string||
|&emsp;&emsp;technicianName||string||
|&emsp;&emsp;rating||integer(int32)||
|&emsp;&emsp;feedback||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;completedAt||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [
		{
			"id": 0,
			"repairNo": "",
			"location": "",
			"issueType": "",
			"description": "",
			"images": [],
			"status": "",
			"technicianName": "",
			"rating": 0,
			"feedback": "",
			"createTime": "",
			"completedAt": ""
		}
	],
	"message": ""
}
```


## 提交报修


**接口地址**:`/api/repairs`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "location": "",
  "issueType": "",
  "description": "",
  "images": []
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|createRepairDTO|CreateRepairDTO|body|true|CreateRepairDTO|CreateRepairDTO|
|&emsp;&emsp;location|||false|string||
|&emsp;&emsp;issueType|||false|string||
|&emsp;&emsp;description|||false|string||
|&emsp;&emsp;images|||false|array|string|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 评价报修


**接口地址**:`/api/repairs/{id}/rate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "rating": 0,
  "feedback": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||
|rateRepairDTO|RateRepairDTO|body|true|RateRepairDTO|RateRepairDTO|
|&emsp;&emsp;rating|||false|integer(int32)||
|&emsp;&emsp;feedback|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取报修详情


**接口地址**:`/api/repairs/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultRepairVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||RepairVO|RepairVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;repairNo||string||
|&emsp;&emsp;location||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;images||array|string|
|&emsp;&emsp;status||string||
|&emsp;&emsp;technicianName||string||
|&emsp;&emsp;rating||integer(int32)||
|&emsp;&emsp;feedback||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;completedAt||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"repairNo": "",
		"location": "",
		"issueType": "",
		"description": "",
		"images": [],
		"status": "",
		"technicianName": "",
		"rating": 0,
		"feedback": "",
		"createTime": "",
		"completedAt": ""
	},
	"message": ""
}
```


# AI助手模块


## 发送消息


**接口地址**:`/api/chat/send`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "message": "",
  "sessionId": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||
|chatRequestDTO|ChatRequestDTO|body|true|ChatRequestDTO|ChatRequestDTO|
|&emsp;&emsp;message|||false|string||
|&emsp;&emsp;sessionId|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultChatResponseVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||ChatResponseVO|ChatResponseVO|
|&emsp;&emsp;reply||string||
|&emsp;&emsp;sessionId||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"reply": "",
		"sessionId": ""
	},
	"message": ""
}
```


## 获取推荐问题


**接口地址**:`/api/chat/suggestions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [],
	"message": ""
}
```


## 获取会话列表


**接口地址**:`/api/chat/sessions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListChatSessionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array|ChatSessionVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;sessionId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [
		{
			"id": 0,
			"sessionId": "",
			"title": "",
			"createTime": "",
			"updateTime": ""
		}
	],
	"message": ""
}
```


## 获取聊天历史


**接口地址**:`/api/chat/history`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sessionId||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageVOChatMessageVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||PageVOChatMessageVO|PageVOChatMessageVO|
|&emsp;&emsp;list||array|ChatMessageVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;role||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;hasMore||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"list": [
			{
				"id": 0,
				"role": "",
				"content": "",
				"createTime": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"hasMore": true
	},
	"message": ""
}
```


## 清除会话历史


**接口地址**:`/api/chat/history/{sessionId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sessionId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


# 活动模块


## 报名活动


**接口地址**:`/api/activities/{id}/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 取消报名


**接口地址**:`/api/activities/{id}/cancel`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||object||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {},
	"message": ""
}
```


## 获取活动列表


**接口地址**:`/api/activities`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|status||query|false|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||
|Authorization||header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageVOActivityVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||PageVOActivityVO|PageVOActivityVO|
|&emsp;&emsp;list||array|ActivityVO|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;coverImage||string||
|&emsp;&emsp;&emsp;&emsp;location||string||
|&emsp;&emsp;&emsp;&emsp;startTime||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;endTime||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;maxParticipants||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;currentParticipants||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;organizerName||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;isRegistered||boolean||
|&emsp;&emsp;&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;hasMore||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"list": [
			{
				"id": 0,
				"title": "",
				"description": "",
				"coverImage": "",
				"location": "",
				"startTime": "",
				"endTime": "",
				"maxParticipants": 0,
				"currentParticipants": 0,
				"organizerName": "",
				"status": "",
				"isRegistered": true,
				"createTime": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"hasMore": true
	},
	"message": ""
}
```


## 获取活动详情


**接口地址**:`/api/activities/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|Authorization||header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultActivityVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||ActivityVO|ActivityVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;coverImage||string||
|&emsp;&emsp;location||string||
|&emsp;&emsp;startTime||string(date-time)||
|&emsp;&emsp;endTime||string(date-time)||
|&emsp;&emsp;maxParticipants||integer(int32)||
|&emsp;&emsp;currentParticipants||integer(int32)||
|&emsp;&emsp;organizerName||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;isRegistered||boolean||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": {
		"id": 0,
		"title": "",
		"description": "",
		"coverImage": "",
		"location": "",
		"startTime": "",
		"endTime": "",
		"maxParticipants": 0,
		"currentParticipants": 0,
		"organizerName": "",
		"status": "",
		"isRegistered": true,
		"createTime": ""
	},
	"message": ""
}
```


## 获取我的活动


**接口地址**:`/api/activities/my`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListActivityVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|success||boolean||
|data||array|ActivityVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;coverImage||string||
|&emsp;&emsp;location||string||
|&emsp;&emsp;startTime||string(date-time)||
|&emsp;&emsp;endTime||string(date-time)||
|&emsp;&emsp;maxParticipants||integer(int32)||
|&emsp;&emsp;currentParticipants||integer(int32)||
|&emsp;&emsp;organizerName||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;isRegistered||boolean||
|&emsp;&emsp;createTime||string(date-time)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"success": true,
	"data": [
		{
			"id": 0,
			"title": "",
			"description": "",
			"coverImage": "",
			"location": "",
			"startTime": "",
			"endTime": "",
			"maxParticipants": 0,
			"currentParticipants": 0,
			"organizerName": "",
			"status": "",
			"isRegistered": true,
			"createTime": ""
		}
	],
	"message": ""
}
```