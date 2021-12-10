const setTip = (that,title='操作成功') => {
	return new Promise((reslove, reject) => {
		that.show({
			title,
			type: 'success',
			duration: '1000'
		})
		setTimeout(() => {
			reslove()
		},1000)
	})
}

export {setTip}