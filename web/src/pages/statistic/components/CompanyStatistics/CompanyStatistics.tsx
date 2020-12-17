import { Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getCompanies, getCompanyStat } from 'store/company/services'
import { Wrapper } from 'store/wrapper'
import { ChartType, IChart, ICompany, ICompanyStat, IConference, ILineData } from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface CompanyStatisticsProps {
  defaultChartType?: ChartType
}

const CompanyStatistics = ({ defaultChartType }: CompanyStatisticsProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [companiesOptions, setCompaniesOptions] = useState<Wrapper<ICompany[]>>({
    isFetching: false,
    value: []
  })

  const getAllCompanies = async () => {
    setCompaniesOptions({ ...companiesOptions, isFetching: true })
    try {
      const response = await getCompanies()
      setCompaniesOptions({ isFetching: false, value: response })
    } catch (err) {
      setCompaniesOptions({ ...companiesOptions, isFetching: false })
      getNotifier('error', snackbarContext)(err.message)
    }
  }

  useEffect(() => {
    getAllCompanies()
    // eslint-disable-next-line
  }, [])

  const handleSubmit = (
    companies: ICompany[],
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (companies.length === 1) {
      if (chartType !== ChartType.LINE) {
        setChartData(undefined)
        setChartType(ChartType.LINE)
      }
      getCompanyStat(companies[0].name).then(stat => {
        setChartData({
          legendX: 'years',
          data: [
            {
              id: companies[0].name,
              data: Object.entries(stat.years).map(v => {
                const [y, count] = v
                return { x: y, y: count } as ILineData
              })
            }
          ]
        })
      })
    } else if (companies.length > 1) {
      if (chartType !== ChartType.BAR) {
        setChartData(undefined)
        setChartType(ChartType.BAR)
      }
      const promises = [] as Promise<ICompanyStat>[]
      companies.forEach(company => {
        promises.push(getCompanyStat(company.name))
      })

      Promise.all(promises).then(stats => {
        const data = stats.map(stat => ({ company: stat.companyName, ...stat.years }))
        setChartData({ indexBy: 'company', legendY: 'count', data })
      })
    }
  }

  return (
    <LoadingWrapper
      type={LoadingType.LINEAR}
      deps={[companiesOptions]}
      backdrop={BackdropType.GLOBAL}
    >
      <StatisticBox defaultChartType={defaultChartType}>
        {({ chartType, setChartData, setChartType }) => (
          <Box>
            <Formik
              initialValues={{
                companies: []
              }}
              onSubmit={({ companies }) =>
                handleSubmit(companies, chartType, setChartData, setChartType)
              }
            >
              {({ values, setFieldValue }) => (
                <Form>
                  <Box className={classes.formField}>
                    <Field
                      name="companies"
                      component={Autocomplete}
                      multiple
                      limitTags={5}
                      id="companies-select"
                      options={companiesOptions.value}
                      getOptionLabel={(option: ICompany) => option.name}
                      filterSelectedOptions
                      disableCloseOnSelect
                      onChange={(_, value: ICompany[]) => setFieldValue('companies', value)}
                      getOptionDisabled={() => values.companies.length >= 5}
                      renderTags={(value: IConference[], getTagProps: any) =>
                        value.map((option, index) => (
                          <Chip
                            className={classes.chip}
                            key={option.name}
                            // avatar={<Avatar alt={option.name} src={option.logo} />}
                            label={`${option.name}`}
                            color="secondary"
                            {...getTagProps({ index })}
                          />
                        ))
                      }
                      // renderOption={(option: IConference) => (
                      //   <div className={classes.option}>
                      //     <Avatar
                      //       className={classes.avatarOption}
                      //       alt={option.name}
                      //       src={option.logo}
                      //     />
                      //     <span className={classes.optionText}>{option.name}</span>
                      //   </div>
                      // )}
                      renderInput={params => (
                        <TextField
                          {...params}
                          variant="outlined"
                          label="Companies"
                          color="secondary"
                          placeholder="Choose up to 5 companies"
                        />
                      )}
                    />
                  </Box>

                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={values.companies.length === 0}
                      onClick={() =>
                        handleSubmit(values.companies, chartType, setChartData, setChartType)
                      }
                    >
                      Apply
                    </Button>
                  </Box>
                </Form>
              )}
            </Formik>
          </Box>
        )}
      </StatisticBox>
    </LoadingWrapper>
  )
}

export default CompanyStatistics
